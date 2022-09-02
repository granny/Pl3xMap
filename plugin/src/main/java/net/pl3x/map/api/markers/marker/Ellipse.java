package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an ellipse marker.
 */
public class Ellipse extends Marker {
    private Point center;
    private Vector radius;

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
    public Ellipse setRadius(Vector radius) {
        Preconditions.checkNotNull(radius, "Ellipse radius is null");
        this.radius = radius;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray data = new JsonArray();
        data.add(getCenter().toJson());
        data.add(getCenter().toJson());
        data.add(vec(getRadius()));
        return data;
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
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getCenter(), getRadius());
    }

    @Override
    public String toString() {
        return "Ellipse{center=" + getCenter() + ",radius=" + getRadius() + ",options=" + getOptions() + "}";
    }
}
