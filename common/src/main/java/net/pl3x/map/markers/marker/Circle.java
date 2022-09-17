package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.JsonObjectWrapper;
import net.pl3x.map.Key;
import net.pl3x.map.markers.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a circle marker.
 */
public class Circle extends Marker<Circle> {
    private Point center;
    private double radius;

    private Circle(@NotNull Key key) {
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
    public Circle(@NotNull Key key, double centerX, double centerZ, double radius) {
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
    public Circle(@NotNull Key key, @NotNull Point center, double radius) {
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
    public static Circle of(@NotNull Key key, double centerX, double centerZ, double radius) {
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
    public static Circle of(@NotNull Key key, @NotNull Point center, double radius) {
        return new Circle(key, center, radius);
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
    public JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("center", getCenter());
        wrapper.addProperty("radius", getRadius());
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
                && Double.compare(getRadius(), other.getRadius()) == 0
                && Objects.equals(getCenter(), other.getCenter())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getOptions(), getRadius(), getCenter());
    }

    @Override
    public String toString() {
        return "Circle{"
                + "key=" + getKey()
                + ",center=" + getCenter()
                + ",radius=" + getRadius()
                + ",options=" + getOptions()
                + "}";
    }
}
