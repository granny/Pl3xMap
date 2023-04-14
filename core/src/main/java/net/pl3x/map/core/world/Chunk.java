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

    protected Chunk(World world, Region region) {
        this.world = world;
        this.region = region;

        this.xPos = 0;
        this.yPos = 0;
        this.zPos = 0;

        this.inhabitedTime = 0;
    }

    protected Chunk(World world, Region region, CompoundTag tag) {
        this.world = world;
        this.region = region;

        this.xPos = tag.getInt("xPos");
        this.yPos = tag.getInt("yPos");
        this.zPos = tag.getInt("zPos");

        this.inhabitedTime = tag.getLong("InhabitedTime");
    }

    @NonNull
    public World getWorld() {
        return this.world;
    }

    @NonNull
    public Region getRegion() {
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

    public abstract int getOceanFloorY(int x, int z);

    @NonNull
    public abstract BlockState getBlockState(int x, int y, int z);

    public abstract int getLight(int x, int y, int z);

    @NonNull
    public abstract Biome getBiome(int x, int y, int z);

    public int getMinY(int x, int z) {
        return 0;
    }

    public int getMaxY(int x, int z) {
        return 255;
    }

    @NonNull
    public abstract Chunk populate();

    @NonNull
    public BlockData[] getData() {
        return this.data;
    }

    @Nullable
    public BlockData getData(int x, int z) {
        return this.data[((z & 0xF) << 4) + (x & 0xF)];
    }

    public static Chunk create(World world, Region region, CompoundTag tag) {
        if (!"full".equals(tag.getString("Status"))) {
            return new EmptyChunk(world, region);
        }
        //int version = tag.getInt("DataVersion");
        //if (version < 2200) return new ChunkAnvil113(world, tag);
        //if (version < 2500) return new ChunkAnvil115(world, tag);
        //if (version < 2844) return new ChunkAnvil116(world, tag);
        return new ChunkAnvil118(world, region, tag);
    }

    public static long getValueFromLongArray(long[] data, int valueIndex, int bitsPerValue) {
        int valuesPerLong = 64 / bitsPerValue;
        int longIndex = valueIndex / valuesPerLong;
        int bitIndex = (valueIndex % valuesPerLong) * bitsPerValue;
        long value = data[longIndex] >>> bitIndex;
        return value & (0xFFFFFFFFFFFFFFFFL >>> -bitsPerValue);
    }

    public static long getValueFromLongStream(long[] data, int valueIndex, int bitsPerValue) {
        int bitIndex = valueIndex * bitsPerValue;
        int firstLong = bitIndex >> 6;
        int bitoffset = bitIndex & 0x3F;
        long value = data[firstLong] >>> bitoffset;
        if (bitoffset > 0 && firstLong + 1 < data.length) {
            long value2 = data[firstLong + 1];
            value2 = value2 << -bitoffset;
            value = value | value2;
        }
        return value & (0xFFFFFFFFFFFFFFFFL >>> -bitsPerValue);
    }

    public static int getByteHalf(int value, boolean largeHalf) {
        value = value & 0xFF;
        if (largeHalf) {
            value = value >> 4;
        }
        value = value & 0xF;
        return value;
    }

    /*private Data getData(int x, int z) {
        return this.data[((z & 0xF) << 4) + (x & 0xF)];
    }

    public int getBlockY(int x, int z) {
        return getData(x, z).blockY;
    }

    public int getFluidY(int x, int z) {
        return getData(x, z).fluidY;
    }

    public BlockState getBlockState(int x, int z) {
        return getData(x, z).blockstate;
    }

    public BlockState getFluidState(int x, int z) {
        return getData(x, z).fluidstate;
    }

    public Biome getBiome(int x, int z) {
        Data data = getData(x, z);
        if (data.biome == null) {
            // calculate real biome
            data.biome = getWorld().getBiomeManager().getBiome(getRegion(), x, data.blockY, z);
        }
        return data.biome;
    }*/

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

        @NonNull
        public BlockState getBlockState() {
            return this.blockstate;
        }

        @Nullable
        public BlockState getFluidState() {
            return this.fluidstate;
        }

        @NonNull
        public Biome getBiome(Region region, int x, int z) {
            if (this.biome == null) {
                // calculate real biome
                this.biome = region.getWorld().getBiomeManager().getBiome(region, x, this.blockY, z);
            }
            return this.biome;
        }
    }
}
