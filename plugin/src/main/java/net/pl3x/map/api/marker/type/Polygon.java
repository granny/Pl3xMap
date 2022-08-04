package net.pl3x.map.api.marker.type;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.api.JsonSerializable;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Polygon marker.
 */
public class Polygon extends Marker {
    private final List<Poly> polys = new ArrayList<>();

    public Polygon() {
        super("poly");
    }

    public Polygon(Poly poly) {
        this();
        addPoly(poly);
    }

    @NotNull
    public Polygon addPoly(@NotNull Poly poly) {
        this.polys.add(poly);
        return this;
    }

    @NotNull
    public Polygon removePoly(@NotNull Poly poly) {
        this.polys.remove(poly);
        return this;
    }

    @NotNull
    public List<Poly> getPolys() {
        return this.polys;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        getPolys().forEach(poly -> json.add(poly.toJson()));
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
        Polygon other = (Polygon) o;
        return Objects.equals(getPolys(), other.getPolys())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }

    @Override
    public String toString() {
        return "Polygon{polys=" + getPolys() + ",options=" + getOptions() + "}";
    }

    /**
     * Represents a Polygon.
     * <p>
     * A polygon requires at least one {@link Ring} to represent
     * the outer polygon shape. Any additional rings will be used
     * to cut out "holes" in the outer polygon.
     */
    public static class Poly implements JsonSerializable {
        private final List<Ring> rings = new ArrayList<>();

        public Poly addRing(Ring ring) {
            this.rings.add(ring);
            return this;
        }

        @NotNull
        public Poly removeRing(@NotNull Ring ring) {
            this.rings.remove(ring);
            return this;
        }

        @NotNull
        public List<Ring> getRings() {
            return this.rings;
        }

        @Override
        @NotNull
        public JsonElement toJson() {
            JsonArray json = new JsonArray();
            getRings().forEach(ring -> json.add(ring.toJson()));
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
            Poly other = (Poly) o;
            return Objects.equals(getRings(), other.getRings());
        }

        @Override
        public int hashCode() {
            return Objects.hash(getRings());
        }

        @Override
        public String toString() {
            return "Poly{rings=" + getRings() + "}";
        }

        /**
         * Represents a Polygon ring.
         * <p>
         * A ring is a list of points used to create a polygon shape.
         * This ring could be used as a Polygon's outer ring, or as
         * inner rings to punch holes into the outer ring.
         * <p>
         * A minimum of 3 points is needed to create a valid and
         * visible polygon on the map.
         */
        public static class Ring implements JsonSerializable {
            private final List<Point> points = new ArrayList<>();

            /**
             * Add a point in this ring.
             * <p>
             * Note: The last point you add does not need to be the
             * same as the first point you added.
             *
             * @param point point to add
             * @return this ring
             */
            public Ring addPoint(Point point) {
                this.points.add(point);
                return this;
            }

            /**
             * Remove a point from this ring.
             *
             * @param point point to remove
             * @return this ring
             */
            @NotNull
            public Ring removePoint(@NotNull Point point) {
                this.points.remove(point);
                return this;
            }

            /**
             * Get the points in this ring.
             *
             * @return list of points
             */
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
    }
}
