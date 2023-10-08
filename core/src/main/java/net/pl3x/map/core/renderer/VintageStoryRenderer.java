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

import java.util.Arrays;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.renderer.heightmap.Heightmap;
import net.pl3x.map.core.renderer.task.RegionScanTask;
import net.pl3x.map.core.util.BlurTool;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.jetbrains.annotations.NotNull;

public class VintageStoryRenderer extends Renderer {
    private final Heightmap heightmap;

    public VintageStoryRenderer(@NotNull RegionScanTask task, @NotNull Builder builder) {
        super(task, builder);
        this.heightmap = Pl3xMap.api().getHeightmapRegistry().get("vintage_story");
    }

    @Override
    public @NotNull Heightmap getHeightmap() {
        return this.heightmap;
    }

    @Override
    public void scanData(@NotNull Region region) {
        int[] pixelMap = new int[512 << 9];
        byte[] shadowMap = new byte[512 << 9];
        Arrays.fill(shadowMap, (byte) 0);

        int cX = region.getX() << 5;
        int cZ = region.getZ() << 5;

        // iterate each chunk in this region
        for (int chunkX = cX; chunkX < cX + 32; chunkX++) {
            int bX = chunkX << 4;
            for (int chunkZ = cZ; chunkZ < cZ + 32; chunkZ++) {
                // skip any blocks that do not need to be rendered due to visibility limits
                if (!getWorld().visibleChunk(chunkX, chunkZ)) {
                    continue;
                }
                int bZ = chunkZ << 4;
                Chunk chunk = region.getChunk(chunkX, chunkZ);
                // iterate each block in this chunk
                for (int blockX = bX; blockX < bX + 16; blockX++) {
                    for (int blockZ = bZ; blockZ < bZ + 16; blockZ++) {
                        Pl3xMap.api().getRegionProcessor().checkPaused();
                        // skip any blocks that do not need to be rendered due to visibility limits
                        if (!getWorld().visibleBlock(blockX, blockZ)) {
                            continue;
                        }

                        Chunk.BlockData data = chunk.getData(blockX, blockZ);
                        if (data == null) {
                            // this shouldn't happen, but just in case...
                            continue;
                        }

                        // get biome once
                        Biome biome = data.getBiome(region, blockX, blockZ);

                        // fix true block color
                        int pixelColor = 0;
                        if (data.getFluidState() == null || region.getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
                            // not flat fluids, we need to draw land
                            pixelColor = Colors.fixBlockColor(region, biome, data.getBlockState(), blockX, blockZ);
                            if (pixelColor != 0) {
                                // fix alpha
                                pixelColor = Colors.setAlpha(0xFF, pixelColor);
                            }
                        }

                        // fix up water color
                        pixelColor = processFluids(region.getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS, region, biome, data, blockX, blockZ, pixelColor);

                        // if there was translucent glass, mix it in here
                        for (int color : data.getGlassColors()) {
                            pixelColor = Colors.blend(color, pixelColor);
                        }

                        int index = ((blockZ & 0x1FF) << 9) + (blockX & 0x1FF);
                        pixelMap[index] = pixelColor;

                        float diff = data.getFluidState() != null ? 1 : CalculateAltitudeDiff(region, blockX, blockZ, data.getBlockY());
                        shadowMap[index] = (byte) (128 * diff - 127);
                    }
                }
            }
        }

        byte[] shadowMapCopy = shadowMap.clone();
        BlurTool.Blur(shadowMap, 512, 512, 2);

        for (int i = 0; i < shadowMap.length; i++) {
            float shadow = ((int) (((shadowMap[i] + 127) / 128F - 1f) * 5)) / 5f;
            shadow += ((((shadowMapCopy[i] + 127) / 128F - 1f) * 5) % 1) / 5f;
            int x = i & 511;
            int z = i >> 9;
            int index = (z << 9) + x;
            getTileImage().setPixel(x, z, pixelMap[index] == 0 ? 0 : (Colors.mul(pixelMap[index], shadow * 1.4F + 1F) | 0xFF << 24));
        }
    }

    @Override
    public void scanBlock(@NotNull Region region, @NotNull Chunk chunk, Chunk.@NotNull BlockData data, int blockX, int blockZ) {
    }

    private float CalculateAltitudeDiff(Region region, int blockX, int blockZ, int blockY) {
        Chunk.BlockData northwest = region.getWorld().getChunk(region, (blockX - 1) >> 4, (blockZ - 1) >> 4).getData(blockX - 1, blockZ - 1);
        Chunk.BlockData northeast = region.getWorld().getChunk(region, blockX >> 4, (blockZ - 1) >> 4).getData(blockX, blockZ - 1);
        Chunk.BlockData southwest = region.getWorld().getChunk(region, (blockX - 1) >> 4, blockZ >> 4).getData(blockX - 1, blockZ);

        int leftTop = blockY - (northwest == null ? blockY : northwest.getBlockY());
        int rightTop = blockY - (northeast == null ? blockY : northeast.getBlockY());
        int leftBot = blockY - (southwest == null ? blockY : southwest.getBlockY());

        int direction = Integer.signum(leftTop) + Integer.signum(rightTop) + Integer.signum(leftBot);
        int steepness = Math.max(Math.max(Math.abs(leftTop), Math.abs(rightTop)), Math.abs(leftBot));
        float slopeFactor = Math.min(0.5F, steepness / 10F) / 1.25F;

        if (direction > 0) {
            return 1.08F + slopeFactor;
        }
        if (direction < 0) {
            return 0.92F - slopeFactor;
        }
        return 1;
    }
}
