package net.pl3x.map.api.markers;

import com.google.gson.JsonElement;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a pair of numbers.
 */
public class Vector implements JsonSerializable {
    private double x;
    private double z;

    /**
     * Create a new vector.
     *
     * @param x x
     * @param z z
     */
    public Vector(double x, double z) {
        this.x = x;
        this.z = z;
    }

    public static Vector of(int x, int z) {
        return new Vector(x, z);
    }

    public static Vector of(double x, double z) {
        return new Vector(x, z);
    }

    /**
     * Get the x.
     *
     * @return x
     */
    public double getX() {
        return this.x;
    }

    /**
     * Set the x.
     *
     * @param x x
     * @return this point
     */
    @NotNull
    public Vector setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Get the z.
     *
     * @return z
     */
    public double getZ() {
        return this.z;
    }

    /**
     * Set the z.
     *
     * @param z z
     * @return this point
     */
    @NotNull
    public Vector setZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(getX());
        wrapper.add(getZ());
        return wrapper.getJsonArray();
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
        Vector other = (Vector) o;
        return Double.compare(getX(), other.getX()) == 0
                && Double.compare(getZ(), other.getZ()) == 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long x = Double.doubleToLongBits(getX());
        result = prime * result + (int) (x ^ (x >>> 32));
        long z = Double.doubleToLongBits(getZ());
        result = prime * result + (int) (z ^ (z >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "Vector{x=" + getX() + ",z=" + getZ() + "}";
    }
}
