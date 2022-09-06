package net.pl3x.map.coordinate;

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
    public BlockCoordinate east() {
        return new BlockCoordinate(getX() + 1, getZ());
    }

    @Override
    public BlockCoordinate north() {
        return new BlockCoordinate(getX(), getZ() - 1);
    }

    @Override
    public BlockCoordinate south() {
        return new BlockCoordinate(getX(), getZ() + 1);
    }

    @Override
    public BlockCoordinate west() {
        return new BlockCoordinate(getX() - 1, getZ());
    }

    @Override
    public String toString() {
        return "BlockCoordinate{x=" + getBlockX() + ",z=" + getBlockZ() + "}";
    }
}
