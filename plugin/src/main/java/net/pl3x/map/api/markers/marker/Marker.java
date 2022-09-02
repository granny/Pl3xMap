package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map marker.
 */
public abstract class Marker implements JsonSerializable {
    public static final Gson GSON = new GsonBuilder()
            //.setPrettyPrinting()
            //.disableHtmlEscaping()
            //.serializeNulls()
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

    public static Marker ellipse(@NotNull Point center, @NotNull Vector radius) {
        return new Ellipse(center, radius);
    }

    public static Marker icon(@NotNull Key image, double x, double z) {
        return icon(image, Point.of(x, z));
    }

    public static Marker icon(@NotNull Key image, @NotNull Point point) {
        return new Icon(image, point);
    }

    public static Marker polygon(@NotNull List<Point> points) {
        return Polygon.of(Poly.of(Ring.of(points)));
    }

    public static Marker polyline(@NotNull List<Point> points) {
        return Polyline.of(Line.of(points));
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
            JsonArray json = new JsonArray();
            json.add(marker.getType());
            json.add(marker.toJson());
            if (marker.getOptions() != null) {
                json.add(marker.getOptions().toJson());
            }
            return json;
        }
    }
}
