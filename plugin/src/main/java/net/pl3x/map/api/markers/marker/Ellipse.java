package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.Vector;
import net.pl3x.map.api.markers.option.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ellipse marker.
 */
public class Ellipse extends Marker {
    private Point center;
    private Vector radius;
    private Double tilt;

    private Ellipse() {
        super("elli");
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radiusX x radius
     * @param radiusZ z radius
     */
    public Ellipse(double centerX, double centerZ, double radiusX, double radiusZ) {
        this(Point.of(centerX, centerZ), Vector.of(radiusX, radiusZ));
    }

    /**
     * Create a new ellipse.
     *
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     */
    public Ellipse(@NotNull Point center, double radiusX, double radiusZ) {
        this(center, Vector.of(radiusX, radiusZ));
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     */
    public Ellipse(double centerX, double centerZ, @NotNull Vector radius) {
        this(Point.of(centerX, centerZ), radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     */
    public Ellipse(@NotNull Point center, @NotNull Vector radius) {
        this();
        setCenter(center);
        setRadius(radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @param tilt    tilt
     */
    public Ellipse(double centerX, double centerZ, double radiusX, double radiusZ, double tilt) {
        this(Point.of(centerX, centerZ), Vector.of(radiusX, radiusZ), tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @param tilt    tilt
     */
    public Ellipse(@NotNull Point center, double radiusX, double radiusZ, double tilt) {
        this(center, Vector.of(radiusX, radiusZ), tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     * @param tilt    tilt
     */
    public Ellipse(double centerX, double centerZ, @NotNull Vector radius, double tilt) {
        this(Point.of(centerX, centerZ), radius, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     * @param tilt   tilt
     */
    public Ellipse(@NotNull Point center, @NotNull Vector radius, double tilt) {
        this();
        setCenter(center);
        setRadius(radius);
        setTilt(tilt);
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
    public static Ellipse of(double centerX, double centerZ, double radiusX, double radiusZ) {
        return new Ellipse(centerX, centerZ, radiusX, radiusZ);
    }

    /**
     * Create a new ellipse.
     *
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @return a new ellipse
     */
    public static Ellipse of(@NotNull Point center, double radiusX, double radiusZ) {
        return new Ellipse(center, radiusX, radiusZ);
    }

    /**
     * Create a new ellipse.
     *
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     * @return a new ellipse
     */
    public static Ellipse of(double centerX, double centerZ, @NotNull Vector radius) {
        return new Ellipse(centerX, centerZ, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     * @return a new ellipse
     */
    public static Ellipse of(@NotNull Point center, @NotNull Vector radius) {
        return new Ellipse(center, radius);
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
    public static Ellipse of(double centerX, double centerZ, double radiusX, double radiusZ, double tilt) {
        return new Ellipse(centerX, centerZ, radiusX, radiusZ, tilt);
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
    public static Ellipse of(@NotNull Point center, double radiusX, double radiusZ, double tilt) {
        return new Ellipse(center, radiusX, radiusZ, tilt);
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
    public static Ellipse of(double centerX, double centerZ, @NotNull Vector radius, double tilt) {
        return new Ellipse(centerX, centerZ, radius, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     * @param tilt   tilt
     * @return a new ellipse
     */
    public static Ellipse of(@NotNull Point center, @NotNull Vector radius, double tilt) {
        return new Ellipse(center, radius, tilt);
    }

    /**
     * Get the center {@link Point} of this ellipse.
     *
     * @return center point
     */
    @NotNull
    public Point getCenter() {
        return this.center;
    }

    /**
     * Set a new center {@link Point} for this ellipse.
     *
     * @param center new center
     * @return this ellipse
     */
    @NotNull
    public Ellipse setCenter(@NotNull Point center) {
        Preconditions.checkNotNull(center, "Ellipse center is null");
        this.center = center;
        return this;
    }

    /**
     * Get the radius for this ellipse.
     *
     * @return radius
     */
    @NotNull
    public Vector getRadius() {
        return this.radius;
    }

    /**
     * Set a new radius for this ellipse.
     *
     * @param radius new radius
     * @return this ellipse
     */
    @NotNull
    public Ellipse setRadius(@NotNull Vector radius) {
        Preconditions.checkNotNull(radius, "Ellipse radius is null");
        this.radius = radius;
        return this;
    }

    /**
     * Get the tilt of this ellipse, in degrees.
     * <p>
     * Defaults to '<code>0</code>' if null.
     *
     * @return tilt
     */
    @Nullable
    public Double getTilt() {
        return this.tilt;
    }

    /**
     * Set the tilt of this ellipse, in degrees.
     * <p>
     * Defaults to '<code>0</code>' if null.
     *
     * @param tilt new tilt
     * @return this ellipse
     */
    @NotNull
    public Ellipse setTilt(@Nullable Double tilt) {
        this.tilt = tilt;
        return this;
    }

    @Override
    @NotNull
    public Ellipse setOptions(@Nullable Options options) {
        return (Ellipse) super.setOptions(options);
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(getCenter());
        wrapper.add(getRadius());
        wrapper.add(getTilt());
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
        Ellipse other = (Ellipse) o;
        return Objects.equals(getRadius(), other.getRadius())
                && Objects.equals(getCenter(), other.getCenter())
                && Objects.equals(getOptions(), other.getOptions())
                && Objects.equals(getTilt(), other.getTilt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getCenter(), getRadius(), getTilt());
    }

    @Override
    public String toString() {
        return "Ellipse{"
                + "center=" + getCenter()
                + ",radius=" + getRadius()
                + ",tile=" + getTilt()
                + ",options=" + getOptions()
                + "}";
    }
}
