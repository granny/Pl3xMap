package net.pl3x.map.api.markers;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a point on the map.
 */
public class Point extends Vector {
    public static final Point ZERO = new Point(0, 0);

    /**
     * Create a new point.
     *
     * @param x x coordinate
     * @param z z coordinate
     */
    public Point(int x, int z) {
        super(x, z);
    }

    public static Point of(int x, int z) {
        return new Point(x, z);
    }

    public static Point of(double x, double z) {
        return of((int) x, (int) z);
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray(2);
        json.add((int) getX());
        json.add((int) getZ());
        return json;
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
        Point other = (Point) o;
        return getX() == other.getX() && getZ() == other.getZ();
    }

    @Override
    public int hashCode() {
        int prime = 1543;
        int result = 1;
        result = prime * result + (int) getX();
        result = prime * result + (int) getZ();
        return result;
    }

    @Override
    public String toString() {
        return "Point{x=" + (int) getX() + ",z=" + (int) getZ() + "}";
    }
}
