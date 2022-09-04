package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.markers.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a rectangle marker.
 */
public class Rectangle extends Marker {
    private Point point1;
    private Point point2;

    /**
     * Create a new rectangle.
     * <p>
     * Minimum and maximum values will be automatically sorted.
     *
     * @param point1 first point
     * @param point2 second point
     */
    public Rectangle(@NotNull Point point1, @NotNull Point point2) {
        super("rect");
        setPoint1(point1);
        setPoint2(point2);
    }

    /**
     * Get the first {@link Point} of this rectangle.
     *
     * @return first point
     */
    @NotNull
    public Point getPoint1() {
        return this.point1;
    }

    /**
     * Set the first {@link Point} of this rectangle.
     *
     * @param point1 first point
     * @return this rectangle
     */
    @NotNull
    public Rectangle setPoint1(@NotNull Point point1) {
        Preconditions.checkNotNull(point1, "Rectangle point1 is null");
        this.point1 = point1;
        return this;
    }

    /**
     * Get the second {@link Point} of this rectangle.
     *
     * @return second point
     */
    @NotNull
    public Point getPoint2() {
        return this.point2;
    }

    /**
     * Set the second {@link Point} of this rectangle.
     *
     * @param point2 second point
     * @return this rectangle
     */
    @NotNull
    public Rectangle setPoint2(@NotNull Point point2) {
        Preconditions.checkNotNull(point1, "Rectangle point2 is null");
        this.point2 = point2;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(getPoint1());
        wrapper.add(getPoint2());
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
        Rectangle other = (Rectangle) o;
        return getPoint1() == other.getPoint1()
                && getPoint2() == other.getPoint2()
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getPoint1(), getPoint2());
    }

    @Override
    public String toString() {
        return "Rectangle{point1=" + getPoint1() + ",point2=" + getPoint2() + ",options=" + getOptions() + "}";
    }
}
