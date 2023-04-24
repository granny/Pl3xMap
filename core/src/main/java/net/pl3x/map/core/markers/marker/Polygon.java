package net.pl3x.map.core.markers.marker;

import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.core.markers.JsonObjectWrapper;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a polygon marker.
 * <p>
 * A polygon requires at least one {@link Polyline} for the
 * outer polygon shape. Any additional polylines will be used
 * to cut out "holes" in the outer polygon shape.
 */
@SuppressWarnings("UnusedReturnValue")
public class Polygon extends Marker<@NonNull Polygon> {
    private final List<@NonNull Polyline> polylines = new ArrayList<>();

    private Polygon(@NonNull String key) {
        super("poly", key);
    }

    /**
     * Create a new polygon.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     */
    public Polygon(@NonNull String key, @NonNull Polyline polyline) {
        this(key);
        addPolyline(polyline);
    }

    /**
     * Create a new polygon.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public Polygon(@NonNull String key, @NonNull Polyline @NonNull ... polylines) {
        this(key);
        addPolyline(polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public Polygon(@NonNull String key, @NonNull Collection<@NonNull Polyline> polylines) {
        this(key);
        addPolyline(polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     * @return a new polygon
     */
    public static @NonNull Polygon of(@NonNull String key, @NonNull Polyline polyline) {
        return new Polygon(key, polyline);
    }

    /**
     * Create a new polygon.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static @NonNull Polygon of(@NonNull String key, @NonNull Polyline @NonNull ... polylines) {
        return new Polygon(key, polylines);
    }

    /**
     * Create a new polygon.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new polygon
     */
    public static @NonNull Polygon of(@NonNull String key, @NonNull Collection<@NonNull Polyline> polylines) {
        return new Polygon(key, polylines);
    }

    /**
     * Get the list of polylines in this polygon.
     *
     * @return list of polylines
     */
    public @NonNull List<@NonNull Polyline> getPolylines() {
        return this.polylines;
    }

    /**
     * Clear the list of polylines in this polygon.
     *
     * @return this polygon
     */
    public @NonNull Polygon clearPolylines() {
        this.polylines.clear();
        return this;
    }

    /**
     * Add a polyline to this polygon.
     *
     * @param polyline polyline to remove
     * @return this polygon
     */
    public @NonNull Polygon addPolyline(@NonNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "Polygon polyline is null");
        this.polylines.add(polyline);
        return this;
    }

    /**
     * Add polylines to this polygon.
     *
     * @param polylines polylines to remove
     * @return this polygon
     */
    public @NonNull Polygon addPolyline(@NonNull Polyline @NonNull ... polylines) {
        Preconditions.checkNotNull(polylines, "Polygon polylines is null");
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
    public @NonNull Polygon addPolyline(@NonNull Collection<@NonNull Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "Polygon polylines is null");
        this.polylines.addAll(polylines);
        return this;
    }

    /**
     * Remove a polyline from this polygon.
     *
     * @param polyline polyline to remove
     * @return this polygon
     */
    public @NonNull Polygon removeLine(@NonNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "Polygon polyline is null");
        this.polylines.remove(polyline);
        return this;
    }

    /**
     * Remove polylines from this polygon.
     *
     * @param polylines polylines to remove
     * @return this polygon
     */
    public @NonNull Polygon removeLine(@NonNull Polyline @NonNull ... polylines) {
        Preconditions.checkNotNull(polylines, "Polygon polylines is null");
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
    public @NonNull Polygon removeLine(@NonNull Collection<@NonNull Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "Polygon polylines is null");
        this.polylines.removeAll(polylines);
        return this;
    }

    @Override
    public @NonNull JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("polylines", getPolylines());
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
        Polygon other = (Polygon) o;
        return getKey().equals(other.getKey())
                && Objects.equals(getPolylines(), other.getPolylines())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getPolylines(), getPane(), getOptions());
    }

    @Override
    public @NonNull String toString() {
        return "Polygon{"
                + "key=" + getKey()
                + ",polylines=" + getPolylines()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
