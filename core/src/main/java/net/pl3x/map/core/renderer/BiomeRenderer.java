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

import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.renderer.task.RegionScanTask;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class BiomeRenderer extends Renderer {
    public BiomeRenderer(@NonNull RegionScanTask task, @NonNull Builder builder) {
        super(task, builder);
    }

    @Override
    public void scanBlock(@NonNull Region region, @NonNull Chunk chunk, Chunk.@NonNull BlockData data, int blockX, int blockZ) {
        int pixelColor = 0;

        if (Colors.getRawBlockColor(data.getBlockState().getBlock()) > 0) {
            // determine the biome
            Biome biome = data.getBiome(region, blockX, blockZ);
            int color = ColorsConfig.BIOME_COLORS.getOrDefault(biome.id(), 0);
            pixelColor = Colors.setAlpha(0xFF, color);

            // work out the heightmap
            if (data.getFluidState() == null) {
                pixelColor = Colors.blend(getHeightmap().getColor(region, blockX, blockZ), pixelColor);
            }
        }

        getTileImage().setPixel(blockX, blockZ, pixelColor);
    }
}
