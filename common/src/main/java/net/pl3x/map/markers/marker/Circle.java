package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.JsonArrayWrapper;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.option.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a circle marker.
 */
public class Circle extends Marker {
    private Point center;
    private double radius;

    private Circle() {
        super("circ");
    }

    /**
     * Create a new circle.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  circle radius
     */
    public Circle(double centerX, double centerZ, double radius) {
        this(Point.of(centerX, centerZ), radius);
    }

    /**
     * Create a new circle.
     *
     * @param center center location
     * @param radius circle radius
     */
    public Circle(@NotNull Point center, double radius) {
        this();
        setCenter(center);
        setRadius(radius);
    }

    /**
     * Create a new circle.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  circle radius
     * @return a new circle
     */
    public static Circle of(double centerX, double centerZ, double radius) {
        return new Circle(centerX, centerZ, radius);
    }

    /**
     * Create a new circle.
     *
     * @param center center location
     * @param radius circle radius
     * @return a new circle
     */
    public static Circle of(@NotNull Point center, double radius) {
        return new Circle(center, radius);
    }

    /**
     * Get the center {@link Point} of this circle.
     *
     * @return center point
     */
    @NotNull
    public Point getCenter() {
        return this.center;
    }

    /**
     * Set a new center {@link Point} for this circle.
     *
     * @param center new center
     * @return this circle
     */
    @NotNull
    public Circle setCenter(@NotNull Point center) {
        Preconditions.checkNotNull(center, "Circle center is null");
        this.center = center;
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
    @NotNull
    public Circle setRadius(double radius) {
        this.radius = radius;
        return this;
    }

    @Override
    @NotNull
    public Circle setOptions(@Nullable Options options) {
        return (Circle) super.setOptions(options);
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(getCenter());
        wrapper.add(getRadius());
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
        Circle other = (Circle) o;
        return Double.compare(getRadius(), other.getRadius()) == 0
                && Objects.equals(getCenter(), other.getCenter())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getRadius(), getCenter());
    }

    @Override
    public String toString() {
        return "Circle{center=" + getCenter() + ",radius=" + getRadius() + ",options=" + getOptions() + "}";
    }
}
