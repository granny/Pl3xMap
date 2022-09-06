package net.pl3x.map.iterator;

import net.pl3x.map.coordinate.BlockCoordinate;

/**
 * In iterator that spirals around center block coordinates in a clockwise pattern
 * <p>
 * Example using a radius of 3:
 * <pre>
 *   30 31 32 33 34 35 36
 *   29 12 13 14 15 16 37
 *   28 11 02 03 04 17 38
 *   27 10 01 00 05 18 39
 *   26 09 08 07 06 19 40
 *   25 24 23 22 21 20 41
 *   48 47 46 45 44 43 42
 * </pre>
 */
public final class BlockSpiralIterator extends SpiralIterator<BlockCoordinate> {
    /**
     * Constructs a new BlockSpiralIterator with given center block coordinates and radius
     *
     * @param centerX center x block coordinate
     * @param centerZ center z block coordinate
     * @param radius  block radius around center
     */
    public BlockSpiralIterator(int centerX, int centerZ, int radius) {
        super(centerX, centerZ, radius);
    }

    @Override
    protected BlockCoordinate getCoordinate(int x, int z) {
        return new BlockCoordinate(x, z);
    }
}
