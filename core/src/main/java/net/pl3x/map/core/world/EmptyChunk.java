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
package net.pl3x.map.core.world;

import org.jetbrains.annotations.NotNull;

public class EmptyChunk extends Chunk {
    protected EmptyChunk(@NotNull World world, @NotNull Region region) {
        super(world, region);
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public @NotNull BlockState getBlockState(int x, int y, int z) {
        return Blocks.AIR.getDefaultState();
    }

    @Override
    public int getLight(int x, int y, int z) {
        return getWorld().getSkylight();
    }

    @Override
    public @NotNull Biome getBiome(int x, int y, int z) {
        return Biome.DEFAULT;
    }

    @Override
    public boolean noHeightmap() {
        return false;
    }

    @Override
    public int getWorldSurfaceY(int x, int z) {
        return 0;
    }

    @Override
    public @NotNull Chunk populate() {
        return this;
    }

    @Override
    public @NotNull String toString() {
        return "EmptyChunk{"
                + "world=" + getWorld()
                + ",xPos=" + getX()
                + ",yPos=" + getY()
                + ",zPos=" + getZ()
                + "}";
    }
}
