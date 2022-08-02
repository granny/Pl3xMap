package net.pl3x.map.render.marker;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.render.marker.data.JsonSerializable;
import net.pl3x.map.render.marker.data.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Polyline marker.
 */
public class Polyline extends Marker {
    private final List<Line> lines = new ArrayList<>();

    public Polyline() {
        super("line");
    }

    public Polyline(Line line) {
        this();
        addLine(line);
    }

    @NotNull
    public Polyline addLine(@NotNull Line line) {
        this.lines.add(line);
        return this;
    }

    @NotNull
    public Polyline removeLine(@NotNull Line line) {
        this.lines.remove(line);
        return this;
    }

    @NotNull
    public List<Line> getLines() {
        return this.lines;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        getLines().forEach(line -> json.add(line.toJson()));
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
        Polyline other = (Polyline) o;
        return Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }

    public static class Line implements JsonSerializable {
        private final List<Point> points = new ArrayList<>();

        public Line addPoint(Point point) {
            this.points.add(point);
            return this;
        }

        @NotNull
        public Line removePoint(@NotNull Point point) {
            this.points.remove(point);
            return this;
        }

        @NotNull
        public List<Point> getPoints() {
            return this.points;
        }

        @Override
        @NotNull
        public JsonElement toJson() {
            JsonArray json = new JsonArray();
            getPoints().forEach(point -> json.add(point.toJson()));
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
            Line other = (Line) o;
            return Objects.equals(getPoints(), other.getPoints());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getPoints());
        }
    }
}
