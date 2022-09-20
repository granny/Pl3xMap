package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import java.util.List;
import net.pl3x.map.JsonSerializable;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.Vector;
import net.pl3x.map.markers.option.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map marker.
 */
public abstract class Marker<T extends Marker<T>> extends Keyed implements JsonSerializable {
    private final String type;
    private String pane;
    private Options options;

    /**
     * Create a new marker.
     *
     * @param type type of marker
     * @param key  identifying key
     */
    public Marker(@NotNull String type, @NotNull Key key) {
        super(key);
        Preconditions.checkNotNull(type, "Marker type is null");
        this.type = type;
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
    public static Circle circle(@NotNull Key key, double centerX, double centerZ, double radius) {
        return Circle.of(key, centerX, centerZ, radius);
    }

    /**
     * Create a new circle.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius circle radius
     * @return a new circle
     */
    public static Circle circle(@NotNull Key key, @NotNull Point center, double radius) {
        return Circle.of(key, center, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, double centerX, double centerZ, double radiusX, double radiusZ) {
        return Ellipse.of(key, centerX, centerZ, radiusX, radiusZ);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, @NotNull Point center, double radiusX, double radiusZ) {
        return Ellipse.of(key, center, radiusX, radiusZ);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, double centerX, double centerZ, @NotNull Vector radius) {
        return Ellipse.of(key, centerX, centerZ, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, @NotNull Point center, @NotNull Vector radius) {
        return Ellipse.of(key, center, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @param tilt    tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, double centerX, double centerZ, double radiusX, double radiusZ, double tilt) {
        return Ellipse.of(key, centerX, centerZ, radiusX, radiusZ, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @param tilt    tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, @NotNull Point center, double radiusX, double radiusZ, double tilt) {
        return Ellipse.of(key, center, radiusX, radiusZ, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     * @param tilt    tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, double centerX, double centerZ, @NotNull Vector radius, double tilt) {
        return Ellipse.of(key, centerX, centerZ, radius, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius radius
     * @param tilt   tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Key key, @NotNull Point center, @NotNull Vector radius, double tilt) {
        return Ellipse.of(key, center, radius, tilt);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @return a new icon
     */
    public static Icon icon(@NotNull Key key, double x, double z, @NotNull Key image) {
        return Icon.of(key, x, z, image);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @return a new icon
     */
    public static Icon icon(@NotNull Key key, @NotNull Point point, @NotNull Key image) {
        return Icon.of(key, point, image);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Key key, double x, double z, @NotNull Key image, double size) {
        return Icon.of(key, x, z, image, size, size);
    }

    /**
     * Create a new icon.
     *
     * @param key    identifying key
     * @param x      icon x location on map
     * @param z      icon z location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Key key, double x, double z, @NotNull Key image, double width, double height) {
        return Icon.of(key, x, z, image, width, height);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Key key, @NotNull Point point, @NotNull Key image, double size) {
        return Icon.of(key, point, image, size, size);
    }

    /**
     * Create a new icon.
     *
     * @param key    identifying key
     * @param point  icon location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Key key, @NotNull Point point, @NotNull Key image, double width, double height) {
        return Icon.of(key, point, image, width, height);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Key key, @NotNull Point point, @NotNull Key image, @Nullable Vector size) {
        return Icon.of(key, point, image, size);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key     identifying key
     * @param polygon polygon to add
     * @return a new multi-polygon
     */
    public static MultiPolygon multiPolygon(@NotNull Key key, @NotNull Polygon polygon) {
        return MultiPolygon.of(key, polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon multiPolygon(@NotNull Key key, @NotNull Polygon @NotNull ... polygons) {
        return MultiPolygon.of(key, polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon multiPolygon(@NotNull Key key, @NotNull List<Polygon> polygons) {
        return MultiPolygon.of(key, polygons);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     * @return a new multi-polyline
     */
    public static MultiPolyline multiPolyline(@NotNull Key key, @NotNull Polyline polyline) {
        return MultiPolyline.of(key, polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline multiPolyline(@NotNull Key key, @NotNull Polyline @NotNull ... polylines) {
        return MultiPolyline.of(key, polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline multiPolyline(@NotNull Key key, @NotNull List<Polyline> polylines) {
        return MultiPolyline.of(key, polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     * @return a new polygon
     */
    public static Polygon polygon(@NotNull Key key, @NotNull Polyline polyline) {
        return Polygon.of(key, polyline);
    }

    /**
     * Create a new polygon.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static Polygon polygon(@NotNull Key key, @NotNull Polyline @NotNull ... polylines) {
        return Polygon.of(key, polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static Polygon polygon(@NotNull Key key, @NotNull List<Polyline> polylines) {
        return Polygon.of(key, polylines);
    }

    /**
     * Create a new polyline.
     *
     * @param key   identifying key
     * @param point point to add
     * @return a new polyline
     */
    public static Polyline polyline(@NotNull Key key, @NotNull Point point) {
        return Polyline.of(key, point);
    }

    /**
     * Create a new polyline.
     *
     * @param key    identifying key
     * @param points points to add
     * @return a new polyline
     */
    public static Polyline polyline(@NotNull Key key, @NotNull Point @NotNull ... points) {
        return Polyline.of(key, points);
    }

    /**
     * Create a new polyline.
     *
     * @param key    identifying key
     * @param points points to add
     * @return a new polyline
     */
    public static Polyline polyline(@NotNull Key key, @NotNull List<Point> points) {
        return Polyline.of(key, points);
    }

    /**
     * Create a new rectangle.
     *
     * @param key identifying key
     * @param x1  first x point
     * @param z1  first z point
     * @param x2  second x point
     * @param z2  second z point
     * @return a new rectangle
     */
    public static Rectangle rectangle(@NotNull Key key, double x1, double z1, double x2, double z2) {
        return Rectangle.of(key, x1, z1, x2, z2);
    }

    /**
     * Create a new rectangle.
     *
     * @param key    identifying key
     * @param point1 first point
     * @param point2 second point
     * @return a new rectangle
     */
    public static Rectangle rectangle(@NotNull Key key, @NotNull Point point1, @NotNull Point point2) {
        return Rectangle.of(key, point1, point2);
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
     * Get the map pane where the marker will be added.
     * <p>
     * Defaults to the overlay pane if null.
     *
     * @return map pane
     */
    @Nullable
    public String getPane() {
        return this.pane;
    }

    /**
     * Set the map pane where the marker will be added.
     * <p>
     * Defaults to the overlay pane if null.
     *
     * @param pane map pane
     * @return this marker
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public T setPane(@Nullable String pane) {
        this.pane = pane;
        return (T) this;
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
    @SuppressWarnings("unchecked")
    public T setOptions(@Nullable Options options) {
        this.options = options;
        return (T) this;
    }

    /**
     * Set new options for this marker.
     * <p>
     * Null options represents "default" values. See wiki about defaults.
     *
     * @param builder new options builder or null
     * @return this marker
     */
    @NotNull
    public T setOptions(@Nullable Options.Builder builder) {
        return setOptions(builder == null ? null : builder.build());
    }
}
