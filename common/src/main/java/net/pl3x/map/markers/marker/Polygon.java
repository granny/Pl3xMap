package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.JsonArrayWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a polygon marker.
 * <p>
 * A polygon requires at least one {@link Polyline} for the
 * outer polygon shape. Any additional polylines will be used
 * to cut out "holes" in the outer polygon shape.
 */
public class Polygon extends Marker<Polygon> {
    private final List<Polyline> polylines = new ArrayList<>();

    private Polygon() {
        super("poly");
    }

    /**
     * Create a new polygon.
     *
     * @param polyline polyline to add
     */
    public Polygon(@NotNull Polyline polyline) {
        this();
        addPolyline(polyline);
    }

    /**
     * Create a new polygon.
     *
     * @param polylines polylines to add
     */
    public Polygon(@NotNull Polyline @NotNull ... polylines) {
        this();
        addPolyline(polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param polylines polylines to add
     */
    public Polygon(@NotNull Collection<Polyline> polylines) {
        this();
        addPolyline(polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param polyline polyline to add
     * @return a new polygon
     */
    public static Polygon of(@NotNull Polyline polyline) {
        return new Polygon(polyline);
    }

    /**
     * Create a new polygon.
     *
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static Polygon of(@NotNull Polyline @NotNull ... polylines) {
        return new Polygon(polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static Polygon of(@NotNull Collection<Polyline> polylines) {
        return new Polygon(polylines);
    }

    /**
     * Get the list of polylines in this polygon.
     *
     * @return list of polylines
     */
    @NotNull
    public List<Polyline> getPolylines() {
        return this.polylines;
    }

    /**
     * Clear the list of polylines in this polygon.
     *
     * @return this polygon
     */
    @NotNull
    public Polygon clearPolylines() {
        this.polylines.clear();
        return this;
    }

    /**
     * Add a polyline to this polygon.
     *
     * @param polyline polyline to remove
     * @return this polygon
     */
    @NotNull
    public Polygon addPolyline(@NotNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "Poly polyline is null");
        this.polylines.add(polyline);
        return this;
    }

    /**
     * Add polylines to this polygon.
     *
     * @param polylines polylines to remove
     * @return this polygon
     */
    @NotNull
    public Polygon addPolyline(@NotNull Polyline @NotNull ... polylines) {
        Preconditions.checkNotNull(polylines, "Poly polylines is null");
        for (Polyline polyline : polylines) {
            addPolyline(polyline);
        }
        return this;
    }

    /**
     * Add polylines to this polygon.
     *
     * @param polylines polylines to remove
     * @return this polygon
     */
    @NotNull
    public Polygon addPolyline(@NotNull Collection<Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "Poly polylines is null");
        this.polylines.addAll(polylines);
        return this;
    }

    /**
     * Remove a polyline from this polygon.
     *
     * @param polyline polyline to remove
     * @return this polygon
     */
    @NotNull
    public Polygon removeLine(@NotNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "Poly polyline is null");
        this.polylines.remove(polyline);
        return this;
    }

    /**
     * Remove polylines from this polygon.
     *
     * @param polylines polylines to remove
     * @return this polygon
     */
    @NotNull
    public Polygon removeLine(@NotNull Polyline @NotNull ... polylines) {
        Preconditions.checkNotNull(polylines, "Poly polylines is null");
        for (Polyline polyline : polylines) {
            removeLine(polyline);
        }
        return this;
    }

    /**
     * Remove polylines from this polygon.
     *
     * @param polylines polylines to remove
     * @return this polygon
     */
    @NotNull
    public Polygon removeLine(@NotNull Collection<Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "Poly polylines is null");
        this.polylines.removeAll(polylines);
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        getPolylines().forEach(wrapper::add);
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
        Polygon other = (Polygon) o;
        return Objects.equals(getPolylines(), other.getPolylines());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPolylines());
    }

    @Override
    public String toString() {
        return "Polygon{polylines=" + getPolylines() + "}";
    }
}
