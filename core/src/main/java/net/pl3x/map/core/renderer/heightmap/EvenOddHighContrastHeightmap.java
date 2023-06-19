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
package net.pl3x.map.core.renderer.heightmap;

import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.jetbrains.annotations.NotNull;

public class EvenOddHighContrastHeightmap extends Heightmap {
    public EvenOddHighContrastHeightmap() {
        super("even_odd_high_contrast");
    }

    public int getMax() {
        return 0x66;
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public int getColor(@NotNull Region region, int blockX, int blockZ) {
        Chunk.BlockData origin = region.getWorld().getChunk(region, blockX >> 4, blockZ >> 4).getData(blockX, blockZ);
        Chunk.BlockData west = region.getWorld().getChunk(region, (blockX - 1) >> 4, blockZ >> 4).getData(blockX - 1, blockZ);
        Chunk.BlockData north = region.getWorld().getChunk(region, blockX >> 4, (blockZ - 1) >> 4).getData(blockX, blockZ - 1);
        int heightColor = 0x33;
        if (origin != null) {
            int y = origin.getBlockY();
            if (west != null) {
                heightColor = getColor(y, west.getBlockY(), heightColor, 0x44);
            }
            if (north != null) {
                heightColor = getColor(y, north.getBlockY(), heightColor, 0x44);
            }
            if (y % 2 == 1) {
                heightColor += 0x11;
            }
        }
        return heightColor << 24;
    }
}
