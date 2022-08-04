package net.pl3x.map.api.marker.type;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ellipse marker.
 */
public class Ellipse extends Marker {
    private Point center;
    private double radiusX;
    private double radiusZ;

    /**
     * Create a new ellipse at 0,0 with horizontal and vertical radius of 10.
     */
    public Ellipse() {
        this(Point.ZERO, 10, 10);
    }

    /**
     * Create a new ellipse.
     *
     * @param center  center location
     * @param radiusX horizontal radius
     * @param radiusZ vertical radius
     */
    public Ellipse(@NotNull Point center, double radiusX, double radiusZ) {
        super("elli");
        setCenter(center);
        setRadiusX(radiusX);
        setRadiusZ(radiusZ);
    }

    /**
     * Get the center point of this ellipse.
     *
     * @return center point
     */
    @NotNull
    public Point getCenter() {
        return this.center;
    }

    /**
     * Set a new center point for this ellipse.
     * <p>
     * Null value will set center to 0,0
     *
     * @param center new center
     * @return this ellipse
     */
    @NotNull
    public Ellipse setCenter(@NotNull Point center) {
        Preconditions.checkNotNull(center);
        this.center = center;
        return this;
    }

    /**
     * Get the horizontal radius of this ellipse.
     *
     * @return horizontal radius
     */
    public double getRadiusX() {
        return this.radiusX;
    }

    /**
     * Set the horizontal radius for this ellipse.
     *
     * @param radius new horizontal radius
     * @return this ellipse
     */
    @NotNull
    public Ellipse setRadiusX(double radius) {
        this.radiusX = radius;
        return this;
    }

    /**
     * Get the vertical radius of this ellipse.
     *
     * @return vertical radius
     */
    public double getRadiusZ() {
        return this.radiusZ;
    }

    /**
     * Set the vertical radius for this ellipse.
     *
     * @param radius new vertical radius
     * @return this ellipse
     */
    @NotNull
    public Ellipse setRadiusZ(double radius) {
        this.radiusZ = radius;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray data = new JsonArray();
        data.add(getCenter().toJson());
        data.add(getCenter().toJson());
        data.add(getRadiusX());
        data.add(getRadiusZ());
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
        return Double.compare(getRadiusX(), other.getRadiusX()) == 0
                && Double.compare(getRadiusZ(), other.getRadiusZ()) == 0
                && Objects.equals(getCenter(), other.getCenter())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getRadiusX(), getRadiusZ(), getCenter());
    }

    @Override
    public String toString() {
        return "Ellipse{center=" + getCenter() + ",radiusX=" + getRadiusX() + "radiusZ=" + getRadiusZ() + ",options=" + getOptions() + "}";
    }
}
