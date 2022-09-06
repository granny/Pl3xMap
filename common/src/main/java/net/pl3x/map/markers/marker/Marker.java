package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import java.util.List;
import net.pl3x.map.JsonSerializable;
import net.pl3x.map.Key;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.Vector;
import net.pl3x.map.markers.option.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a map marker.
 */
public abstract class Marker implements JsonSerializable {
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

    /**
     * Create a new circle.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  circle radius
     * @return a new circle
     */
    public static Circle circle(double centerX, double centerZ, double radius) {
        return Circle.of(centerX, centerZ, radius);
    }

    /**
     * Create a new circle.
     *
     * @param center center location
     * @param radius circle radius
     * @return a new circle
     */
    public static Circle circle(@NotNull Point center, double radius) {
        return Circle.of(center, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(double centerX, double centerZ, double radiusX, double radiusZ) {
        return Ellipse.of(centerX, centerZ, radiusX, radiusZ);
    }

    /**
     * Create a new ellipse.
     *
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Point center, double radiusX, double radiusZ) {
        return Ellipse.of(center, radiusX, radiusZ);
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(double centerX, double centerZ, @NotNull Vector radius) {
        return Ellipse.of(centerX, centerZ, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Point center, @NotNull Vector radius) {
        return Ellipse.of(center, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @param tilt    tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(double centerX, double centerZ, double radiusX, double radiusZ, double tilt) {
        return Ellipse.of(centerX, centerZ, radiusX, radiusZ, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @param tilt    tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Point center, double radiusX, double radiusZ, double tilt) {
        return Ellipse.of(center, radiusX, radiusZ, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     * @param tilt    tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(double centerX, double centerZ, @NotNull Vector radius, double tilt) {
        return Ellipse.of(centerX, centerZ, radius, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     * @param tilt   tilt
     * @return a new ellipse
     */
    public static Ellipse ellipse(@NotNull Point center, @NotNull Vector radius, double tilt) {
        return Ellipse.of(center, radius, tilt);
    }

    /**
     * Create a new icon.
     *
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @return a new icon
     */
    public static Icon icon(double x, double z, @NotNull Key image) {
        return Icon.of(x, z, image);
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @return a new icon
     */
    public static Icon icon(@NotNull Point point, @NotNull Key image) {
        return Icon.of(point, image);
    }

    /**
     * Create a new icon.
     *
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon icon(double x, double z, @NotNull Key image, double size) {
        return Icon.of(x, z, image, size, size);
    }

    /**
     * Create a new icon.
     *
     * @param x      icon x location on map
     * @param z      icon z location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    public static Icon icon(double x, double z, @NotNull Key image, double width, double height) {
        return Icon.of(x, z, image, width, height);
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Point point, @NotNull Key image, double size) {
        return Icon.of(point, image, size, size);
    }

    /**
     * Create a new icon.
     *
     * @param point  icon location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Point point, @NotNull Key image, double width, double height) {
        return Icon.of(point, image, width, height);
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon icon(@NotNull Point point, @NotNull Key image, @Nullable Vector size) {
        return Icon.of(point, image, size);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygon polygon to add
     * @return a new multi-polygon
     */
    public static MultiPolygon multiPolygon(@NotNull Polygon polygon) {
        return MultiPolygon.of(polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon multiPolygon(@NotNull Polygon @NotNull ... polygons) {
        return MultiPolygon.of(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon multiPolygon(@NotNull List<Polygon> polygons) {
        return MultiPolygon.of(polygons);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polyline polyline to add
     * @return a new multi-polyline
     */
    public static MultiPolyline multiPolyline(@NotNull Polyline polyline) {
        return MultiPolyline.of(polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline multiPolyline(@NotNull Polyline @NotNull ... polylines) {
        return MultiPolyline.of(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline multiPolyline(@NotNull List<Polyline> polylines) {
        return MultiPolyline.of(polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param polyline polyline to add
     * @return a new polygon
     */
    public static Polygon polygon(@NotNull Polyline polyline) {
        return Polygon.of(polyline);
    }

    /**
     * Create a new polygon.
     *
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static Polygon polygon(@NotNull Polyline @NotNull ... polylines) {
        return Polygon.of(polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static Polygon polygon(@NotNull List<Polyline> polylines) {
        return Polygon.of(polylines);
    }

    /**
     * Create a new polyline.
     *
     * @param point point to add
     * @return a new polyline
     */
    public static Polyline polyline(@NotNull Point point) {
        return Polyline.of(point);
    }

    /**
     * Create a new polyline.
     *
     * @param points points to add
     * @return a new polyline
     */
    public static Polyline polyline(@NotNull Point @NotNull ... points) {
        return Polyline.of(points);
    }

    /**
     * Create a new polyline.
     *
     * @param points points to add
     * @return a new polyline
     */
    public static Polyline polyline(@NotNull List<Point> points) {
        return Polyline.of(points);
    }

    /**
     * Create a new rectangle.
     *
     * @param x1 first x point
     * @param z1 first z point
     * @param x2 second x point
     * @param z2 second z point
     * @return a new rectangle
     */
    public static Rectangle rectangle(double x1, double z1, double x2, double z2) {
        return Rectangle.of(x1, z1, x2, z2);
    }

    /**
     * Create a new rectangle.
     *
     * @param point1 first point
     * @param point2 second point
     * @return a new rectangle
     */
    public static Rectangle rectangle(@NotNull Point point1, @NotNull Point point2) {
        return Rectangle.of(point1, point2);
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
}
