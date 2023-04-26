/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
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

import java.util.Locale;
import net.pl3x.map.core.Keyed;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.image.TileImage;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.renderer.heightmap.Heightmap;
import net.pl3x.map.core.renderer.task.RegionScanTask;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.BlockState;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Renderer extends Keyed {
    private final RegionScanTask task;
    private final String name;
    private final World world;
    private final Heightmap heightmap;

    private TileImage tileImage;

    public Renderer(@NonNull RegionScanTask task, @NonNull Builder builder) {
        super(builder.key());
        this.task = task;
        this.name = builder.name();
        this.world = task.getWorld();

        String key = getWorld().getConfig().RENDER_HEIGHTMAP_TYPE.toLowerCase(Locale.ROOT);
        this.heightmap = Pl3xMap.api().getHeightmapRegistry().get(key);
    }

    public @NonNull RegionScanTask getRegionScanTask() {
        return this.task;
    }

    public @NonNull World getWorld() {
        return this.world;
    }

    public @NonNull String getName() {
        return this.name;
    }

    public @NonNull Heightmap getHeightmap() {
        return this.heightmap;
    }

    public @NonNull TileImage getTileImage() {
        return this.tileImage;
    }

    public void allocateData(@NonNull Point region) {
        this.tileImage = new TileImage(getKey(), getWorld(), region);
    }

    public void saveData(@NonNull Point region) {
        this.tileImage.saveToDisk();
    }

    public void scanData(@NonNull Region region) {
        int cX = region.getX() << 5;
        int cZ = region.getZ() << 5;

        // iterate each chunk in this region
        for (int chunkX = cX; chunkX < cX + 32; chunkX++) {
            int bX = chunkX << 4;
            for (int chunkZ = cZ; chunkZ < cZ + 32; chunkZ++) {
                int bZ = chunkZ << 4;
                Chunk chunk = region.getChunk(chunkX, chunkZ);
                // iterate each block in this chunk
                for (int blockX = bX; blockX < bX + 16; blockX++) {
                    for (int blockZ = bZ; blockZ < bZ + 16; blockZ++) {
                        if (getWorld().isPaused()) {
                            return;
                        }
                        Chunk.BlockData data = chunk.getData(blockX, blockZ);
                        if (data == null) {
                            // this shouldn't happen, but just in case...
                            continue;
                        }
                        scanBlock(region, chunk, data, blockX, blockZ);
                    }
                }
            }
        }
    }

    public abstract void scanBlock(@NonNull Region region, @NonNull Chunk chunk, Chunk.@NonNull BlockData data, int blockX, int blockZ);

    public int basicPixelColor(@NonNull Region region, @Nullable BlockState blockstate, @Nullable BlockState fluidstate, @NonNull Biome biome, int blockX, int blockY, int blockZ, int fluidY) {
        // fluid stuff
        boolean isFluid = fluidstate != null;
        boolean flatFluid = isFluid && !region.getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS;

        // fix true block color
        int pixelColor = 0;
        if (!flatFluid) {
            // not flat fluids, we need to draw land
            pixelColor = blockstate == null ? 0 : Colors.fixBlockColor(region, biome, blockstate, blockX, blockZ);
            if (pixelColor != 0) {
                // fix alpha
                pixelColor = Colors.setAlpha(0xFF, pixelColor);
                // work out the heightmap
                pixelColor = Colors.blend(getHeightmap().getColor(region, blockX, blockZ), pixelColor);
            }
        }

        // fix up water color
        if (isFluid) {
            if (flatFluid) {
                pixelColor = Colors.getWaterColor(region, biome, blockX, blockZ);
            } else {
                // fancy fluids, yum
                int fluidColor = fancyFluids(region, biome, fluidstate, blockX, blockZ, (fluidY - blockY) * 0.025F);
                pixelColor = Colors.blend(fluidColor, pixelColor);
            }
        }

        // if there was translucent glass, mix it in here
        //for (int color : data.getGlassColors()) {
        //    pixelColor = Colors.blend(color, pixelColor);
        //}

        return pixelColor;
    }

    public int fancyFluids(@NonNull Region region, @NonNull Biome biome, @NonNull BlockState fluidstate, int blockX, int blockZ, float depth) {
        // let's do some maths to get pretty fluid colors based on depth
        int color;
        if (fluidstate.getBlock().isWater()) {
            color = Colors.getWaterColor(region, biome, blockX, blockZ);
            color = Colors.lerpARGB(color, 0xFF000000, Mathf.clamp(0, 0.45F, Easing.cubicOut(depth / 1.5F)));
            color = Colors.setAlpha((int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), color);
        } else {
            // lava
            color = Colors.lerpARGB(fluidstate.getBlock().color(), 0xFF000000, Mathf.clamp(0, 0.3F, Easing.cubicOut(depth / 1.5F)));
            color = Colors.setAlpha(0xFF, color);
        }
        return color;
    }

    public int calculateLight(@NonNull Chunk chunk, @Nullable BlockState fluidState, int blockX, int blockY, int blockZ, int fluidY, int pixelColor) {
        // get light level right above this block
        int blockLight;
        if (fluidState != null && !fluidState.getBlock().isWater()) {
            // not sure why lava isn't returning the correct light levels in the nether
            // maybe a starlight optimization? just return 15 manually.
            blockLight = 15;
        } else {
            blockLight = chunk.getLight(blockX, (fluidState == null ? blockY : fluidY) + 1, blockZ);
        }
        // blocklight in 0-255 range (minus 0x33 for max darkness cap)
        int alpha = (int) (0xCC * Mathf.inverseLerp(4, 15, blockLight));
        // how much darkness to draw in 0-255 range (minus 0x33 for max darkness cap)
        int darkness = Mathf.clamp(0, 0xCC, 0xCC - alpha);
        // mix it into the pixel
        return Colors.blend(darkness << 24, pixelColor);
    }

    public static class Easing {
        public static float cubicOut(float t) {
            return 1F + ((t -= 1F) * t * t);
        }

        public static float quinticOut(float t) {
            return 1F + ((t -= 1F) * t * t * t * t);
        }
    }

    public record Builder(@NonNull String key, @NonNull String name, @NonNull Class<@NonNull ? extends @NonNull Renderer> clazz) {
    }
}
