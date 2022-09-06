package net.pl3x.map.iterator;

import net.pl3x.map.coordinate.RegionCoordinate;

/**
 * In iterator that spirals around center region coordinates in a clockwise pattern
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
public final class RegionSpiralIterator extends SpiralIterator<RegionCoordinate> {
    /**
     * Constructs a new RegionSpiralIterator with given center region coordinates and radius
     *
     * @param centerX center x region coordinate
     * @param centerZ center z region coordinate
     * @param radius  region radius around center
     */
    public RegionSpiralIterator(int centerX, int centerZ, int radius) {
        super(centerX, centerZ, radius);
    }

    @Override
    protected RegionCoordinate getCoordinate(final int x, final int z) {
        return new RegionCoordinate(x, z);
    }
}
