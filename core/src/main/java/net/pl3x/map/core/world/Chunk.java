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

import de.bluecolored.bluenbt.NBTName;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.core.util.Colors;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public abstract class Chunk {

    protected static final int BLOCKS_PER_SECTION = 16 * 16 * 16;
    protected static final int VALUES_PER_HEIGHTMAP = 16 * 16;

    protected static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
    protected static final int[] EMPTY_INT_ARRAY = new int[0];
    protected static final long[] EMPTY_LONG_ARRAY = new long[0];
    protected static final String[] EMPTY_STRING_ARRAY = new String[0];
    protected static final BlockState[] EMPTY_BLOCKSTATE_ARRAY = new BlockState[0];

    private final World world;
    private final Region region;

    private final int dataVersion;

    private final int xPos;
    private final int yPos;
    private final int zPos;

    protected final BlockData[] data = new BlockData[256];

    protected boolean populated;

    protected Chunk(World world, Region region, Data chunkData, int index) {
        this.world = world;
        this.region = region;

        this.dataVersion = chunkData.dataVersion;

        this.xPos = (region.getX() << 5) + (index & 31);
        this.yPos = world.getMinBuildHeight() >> 4;
        this.zPos = (region.getZ() << 5) + (index << 5);
    }

    public World getWorld() {
        return this.world;
    }

    public Region getRegion() {
        return this.region;
    }

    public int getDataVersion() {
        return dataVersion;
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

    public abstract int getMinY();

    public abstract int getMaxY();

    public abstract long getInhabitedTime();

    public abstract boolean isFull();

    public abstract boolean hasWorldSurfaceHeights();

    public boolean noHeightmap() {
        return !hasWorldSurfaceHeights();
    }

    public abstract int getWorldSurfaceY(int x, int z);

    public abstract BlockState getBlockState(int x, int y, int z);

    public abstract int getLight(int x, int y, int z);

    public abstract Biome getBiome(int x, int y, int z);

    public Chunk populate() {
        if (this.populated) {
            return this;
        }

        // scan chunk for relevant data
        // block coordinates for most northwest block in chunk
        int startX = getX() << 4;
        int startZ = getZ() << 4;

        // iterate each block in this chunk
        for (int blockZ = startX; blockZ < startX + 16; blockZ++) {
            for (int blockX = startZ; blockX < startZ + 16; blockX++) {
                BlockData data = new BlockData();
                data.blockY = noHeightmap() ? getMaxY() : getWorldSurfaceY(blockX, blockZ) + 1;

                // if world has ceiling iterate down until we find air
                if (getWorld().hasCeiling()) {
                    data.blockY = getWorld().getLogicalHeight();
                    do {
                        data.blockY -= 1;
                        data.blockstate = getBlockState(blockX, data.blockY, blockZ);
                    } while (data.blockY > getWorld().getMinBuildHeight() && !data.blockstate.getBlock().isAir());
                }

                // iterate down until we find a renderable block
                do {
                    data.blockY -= 1;
                    data.blockstate = getBlockState(blockX, data.blockY, blockZ);
                    if (data.blockstate.getBlock().isFluid()) {
                        if (data.fluidstate == null) {
                            // get fluid information for the top fluid block
                            data.fluidY = data.blockY;
                            data.fluidstate = data.blockstate;
                            // do not get biome here! causes stackoverflow!
                            // instead, biome will be lazy loaded on first get
                            //data.fluidBiome = getWorld().getBiome(blockX, data.fluidY, blockZ);
                        }
                        continue;
                    }

                    if (getWorld().getConfig().RENDER_TRANSLUCENT_GLASS && data.blockstate.getBlock().isGlass()) {
                        // translucent glass. store this color and keep iterating
                        data.glass.addFirst(Colors.setAlpha(0x99, data.blockstate.getBlock().color()));
                        continue;
                    }

                    // test if block is renderable. we ignore blocks with black color
                    if (data.blockstate.getBlock().color() > 0) {
                        break;
                    }
                } while (data.blockY > getWorld().getMinBuildHeight());

                // determine the biome of final block
                // do not get biome here! causes stackoverflow!
                // instead, biome will be lazy loaded on first get
                //data.blockBiome = getWorld().getBiome(blockX, data.blockY, blockZ);

                if (data.blockstate.getBlock().isFlat()) {
                    data.blockY--;
                }

                // save data
                this.data[((blockZ & 0xF) << 4) + (blockX & 0xF)] = data;
            }
        }

        this.populated = true;

        return this;
    }

    public BlockData[] getData() {
        return this.data;
    }

    public @Nullable BlockData getData(int x, int z) {
        return this.data[((z & 0xF) << 4) + (x & 0xF)];
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
    public String toString() {
        return "Chunk{"
                + "world=" + getWorld()
                + ",xPos=" + getX()
                + ",yPos=" + getY()
                + ",zPos=" + getZ()
                + "}";
    }

    public static class BlockData {
        protected int blockY;
        protected int fluidY = 0;
        protected BlockState blockstate;
        protected BlockState fluidstate = null;
        protected Biome biome;

        protected LinkedList<Integer> glass = new LinkedList<>();

        // TODO: kinda jank
        public static BlockData of(int blockY, int fluidY, BlockState blockstate, BlockState fluidstate, LinkedList<Integer> glass) {
            BlockData data = new BlockData();
            data.blockY = blockY;
            data.fluidY = fluidY;
            data.blockstate = blockstate;
            data.fluidstate = fluidstate;
            data.glass = glass;
            return data;
        }

        public int getBlockY() {
            return this.blockY;
        }

        public int getFluidY() {
            return this.fluidY;
        }

        public BlockState getBlockState() {
            return this.blockstate;
        }

        public @Nullable BlockState getFluidState() {
            return this.fluidstate;
        }

        public Biome getBiome(Region region, int x, int z) {
            if (this.biome == null) {
                int y = this.blockY;
                // use fluid block level if fluid
                if (this.fluidstate != null) {
                    y = this.fluidY;
                }
                // calculate real biome
                this.biome = region.getWorld().getBiomeManager().getBiome(region, x, y, z);
            }
            return this.biome;
        }

        public List<Integer> getGlassColors() {
            return this.glass;
        }
    }

    @SuppressWarnings("FieldMayBeFinal")
    public static class Data {

        @NBTName("DataVersion")
        private int dataVersion = 0;

        public int getDataVersion() {
            return dataVersion;
        }

    }

}
