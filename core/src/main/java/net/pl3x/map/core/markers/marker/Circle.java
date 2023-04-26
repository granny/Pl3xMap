/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.markers.marker;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.core.markers.JsonObjectWrapper;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a circle marker.
 */
@SuppressWarnings("UnusedReturnValue")
public class Circle extends Marker<@NonNull Circle> {
    private Point center;
    private double radius;

    private Circle(@NonNull String key) {
        super("circ", key);
    }

    /**
     * Create a new circle.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  circle radius
     */
    public Circle(@NonNull String key, double centerX, double centerZ, double radius) {
        this(key);
        setCenter(Point.of(centerX, centerZ));
        setRadius(radius);
    }

    /**
     * Create a new circle.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius circle radius
     */
    public Circle(@NonNull String key, @NonNull Point center, double radius) {
        this(key);
        setCenter(center);
        setRadius(radius);
    }

    /**
     * Create a new circle.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  circle radius
     * @return a new circle
     */
    public static @NonNull Circle of(@NonNull String key, double centerX, double centerZ, double radius) {
        return new Circle(key, centerX, centerZ, radius);
    }

    /**
     * Create a new circle.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius circle radius
     * @return a new circle
     */
    public static @NonNull Circle of(@NonNull String key, @NonNull Point center, double radius) {
        return new Circle(key, center, radius);
    }

    /**
     * Get the center {@link Point} of this circle.
     *
     * @return center point
     */
    public @NonNull Point getCenter() {
        return this.center;
    }

    /**
     * Set a new center {@link Point} for this circle.
     *
     * @param center new center
     * @return this circle
     */
    public @NonNull Circle setCenter(@NonNull Point center) {
        this.center = Preconditions.checkNotNull(center, "Circle center is null");
        return this;
    }

    /**
     * Get the radius of this circle.
     *
     * @return radius
     */
    public double getRadius() {
        return this.radius;
    }

    /**
     * Set the radius for this circle.
     *
     * @param radius new radius
     * @return this circle
     */
    public @NonNull Circle setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    @Override
    public @NonNull JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("center", getCenter());
        wrapper.addProperty("radius", getRadius());
        wrapper.addProperty("pane", getPane());
        return wrapper.getJsonObject();
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
        Circle other = (Circle) o;
        return getKey().equals(other.getKey())
                && Objects.equals(getCenter(), other.getCenter())
                && Double.compare(getRadius(), other.getRadius()) == 0
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getCenter(), getRadius(), getPane(), getOptions());
    }

    @Override
    public @NonNull String toString() {
        return "Circle{"
                + "key=" + getKey()
                + ",center=" + getCenter()
                + ",radius=" + getRadius()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
