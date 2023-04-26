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
package net.pl3x.map.core.world;

import java.util.Objects;
import net.querz.nbt.tag.CompoundTag;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Chunk {
    private final World world;
    private final Region region;

    private final int xPos;
    private final int yPos;
    private final int zPos;

    private final long inhabitedTime;

    protected final BlockData[] data = new BlockData[256];

    protected boolean populated;

    protected Chunk(@NonNull World world, @NonNull Region region) {
        this.world = world;
        this.region = region;

        this.xPos = 0;
        this.yPos = 0;
        this.zPos = 0;

        this.inhabitedTime = 0;
    }

    protected Chunk(@NonNull World world, @NonNull Region region, @NonNull CompoundTag tag) {
        this.world = world;
        this.region = region;

        this.xPos = tag.getInt("xPos");
        this.yPos = tag.getInt("yPos");
        this.zPos = tag.getInt("zPos");

        this.inhabitedTime = tag.getLong("InhabitedTime");
    }

    public @NonNull World getWorld() {
        return this.world;
    }

    public @NonNull Region getRegion() {
        return this.region;
    }

    public int getX() {
        return this.xPos;
    }

    public int getY() {
        return this.yPos;
    }

    public int getZ() {
        return this.zPos;
    }

    public long getInhabitedTime() {
        return this.inhabitedTime;
    }

    public abstract int getWorldSurfaceY(int x, int z);

    public abstract @NonNull BlockState getBlockState(int x, int y, int z);

    public abstract int getLight(int x, int y, int z);

    public abstract @NonNull Biome getBiome(int x, int y, int z);

    public abstract @NonNull Chunk populate();

    public @NonNull BlockData[] getData() {
        return this.data;
    }

    public @Nullable BlockData getData(int x, int z) {
        return this.data[((z & 0xF) << 4) + (x & 0xF)];
    }

    public static @NonNull Chunk create(@NonNull World world, @NonNull Region region, @NonNull CompoundTag tag) {
        if (!"full".equals(tag.getString("Status"))) {
            return new EmptyChunk(world, region);
        }
        //int version = tag.getInt("DataVersion");
        //if (version < 2200) return new ChunkAnvil113(world, tag);
        //if (version < 2500) return new ChunkAnvil115(world, tag);
        //if (version < 2844) return new ChunkAnvil116(world, tag);
        return new ChunkAnvil118(world, region, tag);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Chunk other = (Chunk) o;
        return getWorld().equals(other.getWorld())
                && getX() == other.getX()
                && getY() == other.getY()
                && getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getWorld(), getX(), getY(), getZ());
    }

    @Override
    public @NonNull String toString() {
        return "Chunk{"
                + "world=" + getWorld()
                + ",xPos=" + getX()
                + ",yPos=" + getY()
                + ",zPos=" + getZ()
                + "}";
    }

    public static class BlockData {
        protected int blockY, fluidY = 0;
        protected BlockState blockstate, fluidstate = null;
        protected Biome biome;

        //protected final LinkedList<Integer> glass = new LinkedList<>();

        public int getBlockY() {
            return this.blockY;
        }

        public int getFluidY() {
            return this.fluidY;
        }

        public @NonNull BlockState getBlockState() {
            return this.blockstate;
        }

        public @Nullable BlockState getFluidState() {
            return this.fluidstate;
        }

        public @NonNull Biome getBiome(@NonNull Region region, int x, int z) {
            if (this.biome == null) {
                // calculate real biome
                this.biome = region.getWorld().getBiomeManager().getBiome(region, x, this.blockY, z);
            }
            return this.biome;
        }
    }
}
