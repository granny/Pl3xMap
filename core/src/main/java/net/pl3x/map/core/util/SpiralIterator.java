package net.pl3x.map.core.util;

import java.util.Iterator;
import java.util.NoSuchElementException;
import net.pl3x.map.core.markers.Point;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * An iterator that spirals around a center point in a clockwise pattern
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
public class SpiralIterator implements Iterator<Point> {
    protected final long totalSteps;

    protected int currentX;
    protected int currentZ;
    protected long currentStep;

    protected long totalStepsInLeg;
    protected long currentStepInLeg;
    protected long legAxis;

    protected Direction direction = Direction.WEST;

    /**
     * Constructs a new SpiralIterator with given center point and radius
     *
     * @param centerX center x point
     * @param centerZ center z point
     * @param radius  radius around center
     */
    public SpiralIterator(int centerX, int centerZ, int radius) {
        this.currentX = centerX;
        this.currentZ = centerZ;
        this.totalSteps = (radius * 2L + 1) * (radius * 2L + 1);
    }

    @Override
    public boolean hasNext() {
        return this.currentStep < this.totalSteps;
    }

    @Override
    public @NonNull Point next() throws NoSuchElementException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }

        // get current coordinate
        Point point = Point.of(this.currentX, this.currentZ);

        // set up for the next coordinate
        switch (this.direction) {
            case SOUTH -> ++this.currentZ;
            case WEST -> --this.currentX;
            case NORTH -> --this.currentZ;
            default -> ++this.currentX;
        }

        // calculate where we are in the spiral
        ++this.currentStep;
        ++this.currentStepInLeg;
        if (this.currentStepInLeg > this.totalStepsInLeg) {
            this.direction = this.direction.next();
            this.currentStepInLeg = 0;
            ++this.legAxis;
            if (this.legAxis > 1) {
                this.legAxis = 0;
                this.totalStepsInLeg++;
            }
        }

        return point;
    }

    /**
     * Represents the cardinal directions
     */
    public enum Direction {
        EAST, SOUTH, WEST, NORTH;

        private static final Direction[] VALUES = values();

        /**
         * Get the next direction in a clockwise pattern
         *
         * @return next direction
         */
        public @NonNull Direction next() {
            return VALUES[(ordinal() + 1) & 3];
        }
    }
}
