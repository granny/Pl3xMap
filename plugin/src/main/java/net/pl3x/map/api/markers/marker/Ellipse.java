package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.Vector;
import net.pl3x.map.api.JsonArrayWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ellipse marker.
 */
public class Ellipse extends Marker {
    private Point center;
    private Vector radius;
    private Double tilt;

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     */
    public Ellipse(@NotNull Point center, @NotNull Vector radius) {
        super("elli");
        setCenter(center);
        setRadius(radius);
    }

    /**
     * Create a new ellipse.
     *
     * @param center center location
     * @param radius radius
     * @param tilt   tilt
     */
    public Ellipse(@NotNull Point center, @NotNull Vector radius, @Nullable Double tilt) {
        super("elli");
        setCenter(center);
        setRadius(radius);
        setTilt(tilt);
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
