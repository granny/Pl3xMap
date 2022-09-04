package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.List;
import net.pl3x.map.api.JsonSerializable;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.markers.Line;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.Poly;
import net.pl3x.map.api.markers.Ring;
import net.pl3x.map.api.markers.Vector;
import net.pl3x.map.api.markers.option.Options;
import net.pl3x.map.api.JsonArrayWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map marker.
 */
public abstract class Marker implements JsonSerializable {
    public static final Gson GSON = new GsonBuilder()
            //.setPrettyPrinting()
            //.disableHtmlEscaping()
            .serializeNulls()
            .registerTypeHierarchyAdapter(Marker.class, new Adapter())
            .setLenient()
            .create();

    private final String type;
    private Options options = null;

    /**
     * Create a new marker.
     *
     * @param type type of marker
     */
    public Marker(@NotNull String type) {
        Preconditions.checkNotNull(type, "Marker type is null");
        this.type = type;
    }

    public static Marker circle(double centerX, double centerZ, double radius) {
        return circle(Point.of(centerX, centerZ), radius);
    }

    public static Marker circle(@NotNull Point center, double radius) {
        return new Circle(center, radius);
    }

    public static Marker ellipse(double centerX, double centerZ, double radiusX, double radiusZ) {
        return ellipse(Point.of(centerX, centerZ), Vector.of(radiusX, radiusZ));
    }

    public static Marker ellipse(double centerX, double centerZ, double radiusX, double radiusZ, double tilt) {
        return ellipse(Point.of(centerX, centerZ), Vector.of(radiusX, radiusZ), tilt);
    }

    public static Marker ellipse(@NotNull Point center, double radiusX, double radiusZ) {
        return ellipse(center, Vector.of(radiusX, radiusZ));
    }

    public static Marker ellipse(@NotNull Point center, double radiusX, double radiusZ, double tilt) {
        return ellipse(center, Vector.of(radiusX, radiusZ), tilt);
    }

    public static Marker ellipse(double centerX, double centerZ, @NotNull Vector radius) {
        return ellipse(Point.of(centerX, centerZ), radius);
    }

    public static Marker ellipse(double centerX, double centerZ, @NotNull Vector radius, double tilt) {
        return ellipse(Point.of(centerX, centerZ), radius, tilt);
    }

    public static Marker ellipse(@NotNull Point center, @NotNull Vector radius) {
        return ellipse(center, radius, null);
    }

    public static Marker ellipse(@NotNull Point center, @NotNull Vector radius, @Nullable Double tilt) {
        return new Ellipse(center, radius, tilt);
    }

    public static Marker icon(double x, double z, @NotNull Key image) {
        return icon(Point.of(x, z), image);
    }

    public static Marker icon(double x, double z, @NotNull Key image, double size) {
        return icon(Point.of(x, z), image, Vector.of(size, size));
    }

    public static Marker icon(double x, double z, @NotNull Key image, double sizeX, double sizeZ) {
        return icon(Point.of(x, z), image, Vector.of(sizeX, sizeZ));
    }

    public static Marker icon(@NotNull Point point, @NotNull Key image) {
        return icon(point, image, null);
    }

    public static Marker icon(@NotNull Point point, @NotNull Key image, double size) {
        return icon(point, image, Vector.of(size, size));
    }

    public static Marker icon(@NotNull Point point, @NotNull Key image, double sizeX, double sizeZ) {
        return icon(point, image, Vector.of(sizeX, sizeZ));
    }

    public static Marker icon(@NotNull Point point, @NotNull Key image, @Nullable Vector size) {
        return new Icon(point, image).setSize(size);
    }

    public static Marker polygon(@NotNull Point @NotNull ... points) {
        return Polygon.of(Poly.of(Ring.of(points)));
    }

    public static Marker polygon(@NotNull List<Point> points) {
        return Polygon.of(Poly.of(Ring.of(points)));
    }

    public static Marker polyline(@NotNull Point @NotNull ... points) {
        return Polyline.of(Line.of(points));
    }

    public static Marker polyline(@NotNull List<Point> points) {
        return Polyline.of(Line.of(points));
    }

    public static Marker polyline(@NotNull Line line) {
        return Polyline.of(line);
    }

    public static Marker multiline(@NotNull Line @NotNull ... lines) {
        return Polyline.of(lines);
    }

    public static Marker multiline(@NotNull List<Line> lines) {
        return Polyline.of(lines);
    }

    public static Marker rectangle(double x1, double z1, double x2, double z2) {
        return rectangle(Point.of(x1, z1), Point.of(x2, z2));
    }

    public static Marker rectangle(@NotNull Point point1, @NotNull Point point2) {
        return new Rectangle(point1, point2);
    }

    /**
     * Get the type identifier of this marker.
     * <p>
     * Used in the serialized json for the frontend.
     *
     * @return marker type
     */
    @NotNull
    public String getType() {
        return this.type;
    }

    /**
     * Get the options of this marker.
     * <p>
     * Null options represents "default" values. See wiki about defaults.
     *
     * @return marker options
     */
    @Nullable
    public Options getOptions() {
        return this.options;
    }

    /**
     * Set new options for this marker.
     * <p>
     * Null options represents "default" values. See wiki about defaults.
     *
     * @param options new options or null
     * @return this marker
     */
    @NotNull
    public Marker setOptions(@Nullable Options options) {
        this.options = options;
        return this;
    }

    /**
     * Serialize this marker into a json string.
     *
     * @return serialized json string
     */
    @NotNull
    public String serialize() {
        return GSON.toJson(this);
    }

    private static class Adapter implements JsonSerializer<Marker> {
        @Override
        @NotNull
        public JsonElement serialize(@NotNull Marker marker, @NotNull Type type, @NotNull JsonSerializationContext context) {
            JsonArrayWrapper wrapper = new JsonArrayWrapper();
            wrapper.add(marker.getType());
            wrapper.add(marker);
            if (marker.getOptions() != null) {
                wrapper.add(marker.getOptions());
            }
            return wrapper.getJsonArray();
        }
    }
}
