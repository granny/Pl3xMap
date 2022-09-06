package net.pl3x.map.coordinate;

/**
 * Represents chunk coordinates
 */
public class ChunkCoordinate extends Coordinate {
    public ChunkCoordinate(int x, int z) {
        super(x, z);
    }

    @Override
    public int getBlockX() {
        return chunkToBlock(getChunkX());
    }

    @Override
    public int getBlockZ() {
        return chunkToBlock(getChunkZ());
    }

    @Override
    public int getChunkX() {
        return getX();
    }

    @Override
    public int getChunkZ() {
        return getZ();
    }

    @Override
    public int getRegionX() {
        return chunkToRegion(getChunkX());
    }

    @Override
    public int getRegionZ() {
        return chunkToRegion(getChunkZ());
    }

    @Override
    public ChunkCoordinate east() {
        return new ChunkCoordinate(getX() + 1, getZ());
    }

    @Override
    public ChunkCoordinate north() {
        return new ChunkCoordinate(getX(), getZ() - 1);
    }

    @Override
    public ChunkCoordinate south() {
        return new ChunkCoordinate(getX(), getZ() + 1);
    }

    @Override
    public ChunkCoordinate west() {
        return new ChunkCoordinate(getX() - 1, getZ());
    }

    @Override
    public String toString() {
        return "ChunkCoordinate{x=" + getChunkX() + ",z=" + getChunkZ() + "}";
    }
}
