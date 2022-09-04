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
 * Represents a line.
 */
public class Line implements JsonSerializable {
    private final List<Point> points = new ArrayList<>();

    public Line(@NotNull Point point) {
        addPoint(point);
    }

    public Line(@NotNull Point @NotNull ... points) {
        addPoint(points);
    }

    public Line(@NotNull Collection<Point> points) {
        addPoint(points);
    }

    public static Line of(@NotNull Point point) {
        return new Line(point);
    }

    public static Line of(@NotNull Point @NotNull ... points) {
        return new Line(points);
    }

    public static Line of(@NotNull Collection<Point> points) {
        return new Line(points);
    }

    @NotNull
    public List<Point> getPoints() {
        return this.points;
    }

    @NotNull
    public Line clearPoints() {
        this.points.clear();
        return this;
    }

    @NotNull
    public Line loop() {
        Preconditions.checkState(this.points.size() > 0, "No points to loop back on");
        Point first = this.points.get(0);
        Point last = this.points.get(this.points.size() - 1);
        Preconditions.checkState(!first.equals(last), "First and last points are the same");
        this.points.add(first);
        return this;
    }

    @NotNull
    public Line addPoint(@NotNull Point point) {
        Preconditions.checkNotNull(point, "Line point is null");
        this.points.add(point);
        return this;
    }

    @NotNull
    public Line addPoint(@NotNull Point @NotNull ... points) {
        Preconditions.checkNotNull(points, "Line points is null");
        addPoint(Arrays.asList(points));
        return this;
    }

    @NotNull
    public Line addPoint(@NotNull Collection<Point> points) {
        Preconditions.checkNotNull(points, "Line points is null");
        this.points.addAll(points);
        return this;
    }

    @NotNull
    public Line removePoint(@NotNull Point point) {
        Preconditions.checkNotNull(point, "Line point is null");
        this.points.remove(point);
        return this;
    }

    @NotNull
    public Line removePoint(@NotNull Point @NotNull ... points) {
        Preconditions.checkNotNull(points, "Line points is null");
        removePoint(Arrays.asList(points));
        return this;
    }

    @NotNull
    public Line removePoint(@NotNull Collection<Point> points) {
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
        Line other = (Line) o;
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
