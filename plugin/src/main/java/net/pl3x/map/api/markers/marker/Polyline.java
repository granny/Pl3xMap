package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.markers.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a line of points for a polyline or polygon shape.
 * <p>
 * This line could be used as a polygon's outer poly,
 * or as inner poly to punch holes into the outer poly.
 * <p>
 * A minimum of 2 {@link Point}s are needed to create a valid and
 * visible line for a polyline on the map.
 * <p>
 * A minimum of 3 {@link Point}s are needed to create a valid and
 * visible line for a polygon on the map.
 * <p>
 * The last point you add does not need to be the
 * same as the first point you added for a polygon.
 */
public class Polyline extends Marker {
    private final List<Point> points = new ArrayList<>();

    private Polyline() {
        super("line");
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param point point to add
     */
    public Polyline(@NotNull Point point) {
        this();
        addPoint(point);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     */
    public Polyline(@NotNull Point @NotNull ... points) {
        this();
        addPoint(points);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     */
    public Polyline(@NotNull Collection<Point> points) {
        this();
        addPoint(points);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param point point to add
     * @return a new line
     */
    public static Polyline of(@NotNull Point point) {
        return new Polyline(point);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     * @return a new line
     */
    public static Polyline of(@NotNull Point @NotNull ... points) {
        return new Polyline(points);
    }

    /**
     * Create a new line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     * @return a new line
     */
    public static Polyline of(@NotNull Collection<Point> points) {
        return new Polyline(points);
    }

    /**
     * Get the list of points in this polyline.
     *
     * @return list of points
     */
    @NotNull
    public List<Point> getPoints() {
        return this.points;
    }

    /**
     * Clear the list of points in this polyline.
     *
     * @return this polyline
     */
    @NotNull
    public Polyline clearPoints() {
        this.points.clear();
        return this;
    }

    /**
     * Add the first point as the end point, closing off the shape.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @return this line
     */
    @NotNull
    public Polyline loop() {
        Preconditions.checkState(this.points.size() > 0, "No points to loop back on");
        Point first = this.points.get(0);
        Point last = this.points.get(this.points.size() - 1);
        Preconditions.checkState(!first.equals(last), "First and last points are the same");
        this.points.add(first);
        return this;
    }

    /**
     * Add a point to this line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param point point to add
     * @return this line
     */
    @NotNull
    public Polyline addPoint(@NotNull Point point) {
        Preconditions.checkNotNull(point, "Line point is null");
        this.points.add(point);
        return this;
    }

    /**
     * Add points to this line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     * @return this line
     */
    @NotNull
    public Polyline addPoint(@NotNull Point @NotNull ... points) {
        Preconditions.checkNotNull(points, "Line points is null");
        for (Point point : points) {
            addPoint(point);
        }
        return this;
    }

    /**
     * Add points to this line.
     * <p>
     * The last point you add does not need to be the
     * same as the first point you added for a polygon.
     *
     * @param points points to add
     * @return this line
     */
    @NotNull
    public Polyline addPoint(@NotNull Collection<Point> points) {
        Preconditions.checkNotNull(points, "Line points is null");
        this.points.addAll(points);
        return this;
    }

    /**
     * Remove a point from this polyline.
     *
     * @param point point to remove
     * @return this polyline
     */
    @NotNull
    public Polyline removePoint(@NotNull Point point) {
        Preconditions.checkNotNull(point, "Line point is null");
        this.points.remove(point);
        return this;
    }

    /**
     * Remove points from this polyline.
     *
     * @param points points to remove
     * @return this polyline
     */
    @NotNull
    public Polyline removePoint(@NotNull Point @NotNull ... points) {
        Preconditions.checkNotNull(points, "Line points is null");
        for (Point point : points) {
            removePoint(point);
        }
        return this;
    }

    /**
     * Remove points from this polyline.
     *
     * @param points points to remove
     * @return this polyline
     */
    @NotNull
    public Polyline removePoint(@NotNull Collection<Point> points) {
        Preconditions.checkNotNull(points, "Line points is null");
        this.points.removeAll(points);
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        getPoints().forEach(wrapper::add);
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
        Polyline other = (Polyline) o;
        return Objects.equals(getPoints(), other.getPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPoints());
    }

    @Override
    public String toString() {
        return "Line{points=" + getPoints() + "}";
    }
}
