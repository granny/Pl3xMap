package net.pl3x.map.render.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.render.marker.data.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Rectangle marker.
 */
public class Rectangle extends Marker {
    private Point point1;
    private Point point2;

    /**
     * Create a new rectangle at -5,-5 -> 5,5
     */
    public Rectangle() {
        this(new Point(-5, -5), new Point(5, 5));
    }

    /**
     * Create a new rectangle.
     *
     * @param x1 first x
     * @param z1 first z
     * @param x2 second x
     * @param z2 second z
     */
    public Rectangle(int x1, int z1, int x2, int z2) {
        this(new Point(x1, z1), new Point(x2, z2));
    }

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
        Preconditions.checkNotNull(point1);
        Preconditions.checkNotNull(point2);
        setPoint1(point1);
        setPoint2(point2);
    }

    /**
     * Get the first point of this rectangle.
     *
     * @return first point
     */
    @NotNull
    public Point getPoint1() {
        return this.point1;
    }

    /**
     * Set the first point of this rectangle.
     *
     * @param point1 first point
     * @return this rectangle
     */
    @NotNull
    public Rectangle setPoint1(@NotNull Point point1) {
        this.point1 = point1;
        return this;
    }

    /**
     * Get the second point of this rectangle.
     *
     * @return second point
     */
    @NotNull
    public Point getPoint2() {
        return this.point2;
    }

    /**
     * Set the second point of this rectangle.
     *
     * @param point2 second point
     * @return this rectangle
     */
    @NotNull
    public Rectangle setPoint2(@NotNull Point point2) {
        this.point2 = point2;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray data = new JsonArray();
        data.add(getPoint1().toJson());
        data.add(getPoint2().toJson());
        return data;
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
}
