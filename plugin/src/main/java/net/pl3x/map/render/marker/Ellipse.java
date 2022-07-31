package net.pl3x.map.render.marker;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.pl3x.map.render.marker.data.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Ellipse marker.
 */
public class Ellipse extends Marker {
    private Point center;
    private double radiusX;
    private double radiusZ;

    public Ellipse() {
        this(Point.ZERO, 0, 0);
    }

    public Ellipse(@NotNull Point center, double radiusX, double radiusZ) {
        setCenter(center);
        setRadiusX(radiusX);
        setRadiusZ(radiusZ);
    }

    /**
     * Get the center point of this ellipse.
     *
     * @return center point
     */
    @Nullable
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
     * Get the radiusX of this ellipse.
     *
     * @return radiusX
     */
    public double getRadiusX() {
        return this.radiusX;
    }

    /**
     * Set the radiusX for this ellipse.
     *
     * @param radius new radiusX
     * @return this ellipse
     */
    @NotNull
    public Ellipse setRadiusX(double radius) {
        this.radiusX = radius;
        return this;
    }

    /**
     * Get the radiusZ of this ellipse.
     *
     * @return radiusZ
     */
    public double getRadiusZ() {
        return this.radiusZ;
    }

    /**
     * Set the radiusZ for this ellipse.
     *
     * @param radius new radiusZ
     * @return this ellipse
     */
    @NotNull
    public Ellipse setRadiusZ(double radius) {
        this.radiusZ = radius;
        return this;
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
                && getOptions().equals(other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getRadiusX(), getRadiusZ(), getCenter());
    }
}
