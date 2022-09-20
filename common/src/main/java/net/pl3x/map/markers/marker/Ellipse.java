package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.JsonObjectWrapper;
import net.pl3x.map.Key;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ellipse marker.
 */
public class Ellipse extends Marker<Ellipse> {
    private Point center;
    private Vector radius;
    private Double tilt;

    private Ellipse(@NotNull Key key) {
        super("elli", key);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radiusX x radius
     * @param radiusZ z radius
     */
    public Ellipse(@NotNull Key key, double centerX, double centerZ, double radiusX, double radiusZ) {
        this(key, Point.of(centerX, centerZ), Vector.of(radiusX, radiusZ));
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     */
    public Ellipse(@NotNull Key key, @NotNull Point center, double radiusX, double radiusZ) {
        this(key, center, Vector.of(radiusX, radiusZ));
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     */
    public Ellipse(@NotNull Key key, double centerX, double centerZ, @NotNull Vector radius) {
        this(key, Point.of(centerX, centerZ), radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius radius
     */
    public Ellipse(@NotNull Key key, @NotNull Point center, @NotNull Vector radius) {
        this(key);
        setCenter(center);
        setRadius(radius);
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
     */
    public Ellipse(@NotNull Key key, double centerX, double centerZ, double radiusX, double radiusZ, double tilt) {
        this(key, Point.of(centerX, centerZ), Vector.of(radiusX, radiusZ), tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param center  center location
     * @param radiusX x radius
     * @param radiusZ z radius
     * @param tilt    tilt
     */
    public Ellipse(@NotNull Key key, @NotNull Point center, double radiusX, double radiusZ, double tilt) {
        this(key, center, Vector.of(radiusX, radiusZ), tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param key     identifying key
     * @param centerX center x location
     * @param centerZ center z location
     * @param radius  radius
     * @param tilt    tilt
     */
    public Ellipse(@NotNull Key key, double centerX, double centerZ, @NotNull Vector radius, double tilt) {
        this(key, Point.of(centerX, centerZ), radius, tilt);
    }

    /**
     * Create a new ellipse.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius radius
     * @param tilt   tilt
     */
    public Ellipse(@NotNull Key key, @NotNull Point center, @NotNull Vector radius, double tilt) {
        this(key);
        setCenter(center);
        setRadius(radius);
        setTilt(tilt);
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
    public static Ellipse of(@NotNull Key key, double centerX, double centerZ, double radiusX, double radiusZ) {
        return new Ellipse(key, centerX, centerZ, radiusX, radiusZ);
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
    public static Ellipse of(@NotNull Key key, @NotNull Point center, double radiusX, double radiusZ) {
        return new Ellipse(key, center, radiusX, radiusZ);
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
    public static Ellipse of(@NotNull Key key, double centerX, double centerZ, @NotNull Vector radius) {
        return new Ellipse(key, centerX, centerZ, radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param key    identifying key
     * @param center center location
     * @param radius radius
     * @return a new ellipse
     */
    public static Ellipse of(@NotNull Key key, @NotNull Point center, @NotNull Vector radius) {
        return new Ellipse(key, center, radius);
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
    public static Ellipse of(@NotNull Key key, double centerX, double centerZ, double radiusX, double radiusZ, double tilt) {
        return new Ellipse(key, centerX, centerZ, radiusX, radiusZ, tilt);
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
    public static Ellipse of(@NotNull Key key, @NotNull Point center, double radiusX, double radiusZ, double tilt) {
        return new Ellipse(key, center, radiusX, radiusZ, tilt);
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
    public static Ellipse of(@NotNull Key key, double centerX, double centerZ, @NotNull Vector radius, double tilt) {
        return new Ellipse(key, centerX, centerZ, radius, tilt);
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
    public static Ellipse of(@NotNull Key key, @NotNull Point center, @NotNull Vector radius, double tilt) {
        return new Ellipse(key, center, radius, tilt);
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
    public JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("center", getCenter());
        wrapper.addProperty("radius", getRadius());
        wrapper.addProperty("tilt", getTilt());
        wrapper.addProperty("pane", getPane());
        return wrapper.getJsonObject();
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
        return getKey().equals(other.getKey())
                && Objects.equals(getCenter(), other.getCenter())
                && Objects.equals(getRadius(), other.getRadius())
                && Objects.equals(getTilt(), other.getTilt())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getCenter(), getRadius(), getTilt(), getPane(), getOptions());
    }

    @Override
    public String toString() {
        return "Ellipse{"
                + "key=" + getKey()
                + ",center=" + getCenter()
                + ",radius=" + getRadius()
                + ",tile=" + getTilt()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
