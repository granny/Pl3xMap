package net.pl3x.map.renderer.iterator.coordinate;

/**
 * Represents block coordinates
 */
public class BlockCoordinate extends Coordinate {
    public BlockCoordinate(int x, int z) {
        super(x, z);
    }

    @Override
    public int getBlockX() {
        return getX();
    }

    @Override
    public int getBlockZ() {
        return getZ();
    }

    @Override
    public int getChunkX() {
        return blockToChunk(getBlockX());
    }

    @Override
    public int getChunkZ() {
        return blockToChunk(getBlockZ());
    }

    @Override
    public int getRegionX() {
        return blockToRegion(getBlockX());
    }

    @Override
    public int getRegionZ() {
        return blockToRegion(getBlockZ());
    }

    @Override
    public String toString() {
        return "BlockCoordinate{x=" + getBlockX() + ",z=" + getBlockZ() + "}";
    }
}
