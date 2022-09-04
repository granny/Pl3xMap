package net.pl3x.map.api.markers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a ring in a {@link Poly}.
 * <p>
 * A ring is a list of points used to create a polygon shape.
 * This ring could be used as a {@link Poly}'s outer ring, or as
 * inner rings to punch holes into the outer ring.
 * <p>
 * A minimum of 3 {@link Point}s are needed to create a valid and
 * visible polygon on the map.
 */
public class Ring implements JsonSerializable {
    private final List<Point> points = new ArrayList<>();

    public Ring(@NotNull Point point) {
        addPoint(point);
    }

    public Ring(@NotNull Point @NotNull ... points) {
        addPoint(points);
    }

    public Ring(@NotNull Collection<Point> points) {
        addPoint(points);
    }

    public static Ring of(@NotNull Point point) {
        return new Ring(point);
    }

    public static Ring of(@NotNull Point @NotNull ... points) {
        return new Ring(points);
    }

    public static Ring of(@NotNull Collection<Point> points) {
        return new Ring(points);
    }

    /**
     * Get the {@link Point}s in this ring.
     *
     * @return list of points
     */
    @NotNull
    public List<Point> getPoints() {
        return this.points;
    }

    @NotNull
    public Ring clearPoints() {
        this.points.clear();
        return this;
    }

    /**
     * Add a {@link Point} to this ring.
     * <p>
     * Note: The last point you add does not need to be the
     * same as the first point you added.
     *
     * @param point point to add
     * @return this ring
     */
    @NotNull
    public Ring addPoint(@NotNull Point point) {
        Preconditions.checkNotNull(point, "Ring point is null");
        this.points.add(point);
        return this;
    }

    /**
     * Add {@link Point}s to this ring.
     * <p>
     * Note: The last point you add does not need to be the
     * same as the first point you added.
     *
     * @param points points to add
     * @return this ring
     */
    @NotNull
    public Ring addPoint(@NotNull Point @NotNull ... points) {
        Preconditions.checkNotNull(points, "Ring points is null");
        addPoint(Arrays.asList(points));
        return this;
    }

    /**
     * Add {@link Point}s to this ring.
     * <p>
     * Note: The last point you add does not need to be the
     * same as the first point you added.
     *
     * @param points points to add
     * @return this ring
     */
    @NotNull
    public Ring addPoint(@NotNull Collection<Point> points) {
        Preconditions.checkNotNull(points, "Ring points is null");
        this.points.addAll(points);
        return this;
    }

    /**
     * Remove a {@link Point} from this ring.
     *
     * @param point point to remove
     * @return this ring
     */
    @NotNull
    public Ring removePoint(@NotNull Point point) {
        Preconditions.checkNotNull(point, "Ring point is null");
        this.points.remove(point);
        return this;
    }

    /**
     * Remove {@link Point}s from this ring.
     *
     * @param points points to remove
     * @return this ring
     */
    @NotNull
    public Ring removePoint(@NotNull Point @NotNull ... points) {
        Preconditions.checkNotNull(points, "Ring points is null");
        removePoint(Arrays.asList(points));
        return this;
    }

    /**
     * Remove {@link Point}s from this ring.
     *
     * @param points points to remove
     * @return this ring
     */
    @NotNull
    public Ring removePoint(@NotNull Collection<Point> points) {
        Preconditions.checkNotNull(points, "Ring points is null");
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
        Ring other = (Ring) o;
        return Objects.equals(getPoints(), other.getPoints());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPoints());
    }

    @Override
    public String toString() {
        return "Ring{points=" + getPoints() + "}";
    }
}
