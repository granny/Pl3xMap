/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.renderer;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.renderer.heightmap.Heightmap;
import net.pl3x.map.core.renderer.task.RegionScanTask;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.BlockState;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.EmptyChunk;
import net.pl3x.map.core.world.Region;
import org.jetbrains.annotations.NotNull;

public class VanillaRenderer extends Renderer {
    private final Heightmap heightmap;

    public VanillaRenderer(@NotNull RegionScanTask task, @NotNull Builder builder) {
        super(task, builder);
        this.heightmap = Pl3xMap.api().getHeightmapRegistry().get("old_school");
    }

    @Override
    public @NotNull Heightmap getHeightmap() {
        return this.heightmap;
    }

    @Override
    public void scanData(@NotNull Region region) {
        int startX = region.getX() << 9;
        int startZ = region.getZ() << 9;

        for (int pixelX = 0; pixelX < 512; pixelX++) {
            int blockX = startX + pixelX;
            double lastBlockY = 0.0D;
            for (int pixelZ = -1; pixelZ < 512; pixelZ++) {
                int blockZ = startZ + pixelZ;

                if (!getWorld().visibleBlock(blockX, blockZ)) {
                    continue;
                }

                Pl3xMap.api().getRegionProcessor().checkPaused();

                Chunk chunk = region.getWorld().getChunk(region, blockX >> 4, blockZ >> 4);
                if (chunk instanceof EmptyChunk) {
                    continue;
                }

                int blockY = chunk.noHeightmap() ? getWorld().getMaxBuildHeight() : chunk.getWorldSurfaceY(blockX, blockZ) + 1;
                int fluidY = 0;
                BlockState blockstate;
                BlockState fluidstate = null;

                // if world has ceiling iterate down until we find air
                blockY = getBlockBelowCeiling(blockY, chunk, blockX, blockZ);

                // iterate down until we find a renderable block
                do {
                    blockY -= 1;
                    blockstate = chunk.getBlockState(blockX, blockY, blockZ);
                    if (blockstate.getBlock().isFluid()) {
                        if (fluidstate == null) {
                            // get fluid information for the top fluid block
                            fluidY = blockY;
                            fluidstate = blockstate;
                        }
                        continue;
                    }

                    // test if block is renderable. we ignore blocks with black color
                    if (blockstate.getBlock().vanilla() > 0) {
                        break;
                    }
                } while (blockY > getWorld().getMinBuildHeight());

                setPixel(pixelZ, fluidstate, fluidY, blockY, pixelX, blockstate, lastBlockY);

                if (blockstate.getBlock().isFlat()) {
                    blockY--;
                }

                lastBlockY = blockY;
            }
        }
    }

    private void setPixel(int pixelZ, BlockState fluidstate, int fluidY, int blockY, int pixelX, BlockState blockstate, double lastBlockY) {
        if (pixelZ >= 0) {
            int color;
            int brightness;
            if (fluidstate != null) {
                color = fluidstate.getBlock().vanilla();
                double heightDiff = (double) (fluidY - blockY) * 0.1D + (double) (pixelX + pixelZ & 1) * 0.2D;
                if (heightDiff < 0.5D) {
                    brightness = 0x00;
                } else if (heightDiff > 0.9D) {
                    brightness = 0x44;
                } else {
                    brightness = 0x22;
                }
            } else {
                color = blockstate.getBlock().vanilla();
                double heightDiff = (blockY - lastBlockY) * 4.0D / (double) (1 + 4) + ((double) (pixelX + pixelZ & 1) - 0.5D) * 0.4D;
                if (heightDiff > 0.6D) {
                    brightness = 0x00;
                } else if (heightDiff < -0.6D) {
                    brightness = 0x44;
                } else {
                    brightness = 0x22;
                }
            }

            getTileImage().setPixel(pixelX, pixelZ, Colors.blend(brightness << 24, Colors.setAlpha(0xFF, color)));
        }
    }

    private int getBlockBelowCeiling(int blockY, Chunk chunk, int blockX, int blockZ) {
        BlockState blockstate;
        if (getWorld().hasCeiling()) {
            blockY = getWorld().getLogicalHeight();
            do {
                blockY -= 1;
                blockstate = chunk.getBlockState(blockX, blockY, blockZ);
            } while (blockY > getWorld().getMinBuildHeight() && !blockstate.getBlock().isAir());
        }
        return blockY;
    }

    @Override
    public void scanBlock(@NotNull Region region, @NotNull Chunk chunk, Chunk.@NotNull BlockData data, int blockX, int blockZ) {
    }
}
