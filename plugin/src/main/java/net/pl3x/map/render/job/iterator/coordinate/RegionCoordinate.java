package net.pl3x.map.render.job.iterator.coordinate;

/**
 * Represents region coordinates
 */
public class RegionCoordinate extends Coordinate {
    public RegionCoordinate(int x, int z) {
        super(x, z);
    }

    @Override
    public int getBlockX() {
        return regionToBlock(getRegionX());
    }

    @Override
    public int getBlockZ() {
        return regionToBlock(getRegionZ());
    }

    @Override
    public int getChunkX() {
        return regionToChunk(getRegionX());
    }

    @Override
    public int getChunkZ() {
        return regionToChunk(getRegionZ());
    }

    @Override
    public int getRegionX() {
        return getX();
    }

    @Override
    public int getRegionZ() {
        return getZ();
    }

    @Override
    public String toString() {
        return "RegionCoordinate{x=" + getRegionX() + ",z=" + getRegionZ() + "}";
    }
}
