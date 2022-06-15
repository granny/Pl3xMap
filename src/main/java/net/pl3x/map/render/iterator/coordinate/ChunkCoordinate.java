package net.pl3x.map.render.iterator.coordinate;

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
    public String toString() {
        return "ChunkCoordinate{x=" + getChunkX() + ",z=" + getChunkZ() + "}";
    }
}
