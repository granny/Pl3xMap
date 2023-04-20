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
 * Represents a multi-polyline marker.
 */
@SuppressWarnings("UnusedReturnValue")
public class MultiPolyline extends Marker<MultiPolyline> {
    private final List<Polyline> polylines = new ArrayList<>();

    private MultiPolyline(@NonNull String key) {
        super("multiline", key);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     */
    public MultiPolyline(@NonNull String key, @NonNull Polyline polyline) {
        this(key);
        addPolyline(polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public MultiPolyline(@NonNull String key, @NonNull Polyline @NonNull ... polylines) {
        this(key);
        addPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public MultiPolyline(@NonNull String key, @NonNull Collection<Polyline> polylines) {
        this(key);
        addPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     * @return a new multi-polyline
     */
    @NonNull
    public static MultiPolyline of(@NonNull String key, @NonNull Polyline polyline) {
        return new MultiPolyline(key, polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    @NonNull
    public static MultiPolyline of(@NonNull String key, @NonNull Polyline @NonNull ... polylines) {
        return new MultiPolyline(key, polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    @NonNull
    public static MultiPolyline of(@NonNull String key, @NonNull Collection<Polyline> polylines) {
        return new MultiPolyline(key, polylines);
    }

    /**
     * Get the list of polylines in this multi-polyline.
     *
     * @return list of polylines
     */
    @NonNull
    public List<Polyline> getPolylines() {
        return this.polylines;
    }

    /**
     * Clear the list of polylines in this multi-polyline.
     *
     * @return this multi-polyline
     */
    @NonNull
    public MultiPolyline clearPolylines() {
        this.polylines.clear();
        return this;
    }

    /**
     * Add a polyline to this multi-polyline.
     *
     * @param polyline polyline to add
     * @return this multi-polyline
     */
    @NonNull
    public MultiPolyline addPolyline(@NonNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "MultiPolyline polyline is null");
        this.polylines.add(polyline);
        return this;
    }

    /**
     * Add polylines to this multi-polyline.
     *
     * @param polylines polylines to add
     * @return this multi-polyline
     */
    @NonNull
    public MultiPolyline addPolyline(@NonNull Polyline @NonNull ... polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        for (Polyline polyline : polylines) {
            addPolyline(polyline);
        }
        return this;
    }

    /**
     * Add polylines to this multi-polyline.
     *
     * @param polylines polylines to add
     * @return this multi-polyline
     */
    @NonNull
    public MultiPolyline addPolyline(@NonNull Collection<Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        this.polylines.addAll(polylines);
        return this;
    }

    /**
     * Remove a polyline from this multi-polyline.
     *
     * @param polyline polyline to remove
     * @return this multi-polyline
     */
    @NonNull
    public MultiPolyline removePolyline(@NonNull Polyline polyline) {
        Preconditions.checkNotNull(polyline, "MultiPolyline polyline is null");
        this.polylines.remove(polyline);
        return this;
    }

    /**
     * Remove polylines from this multi-polyline.
     *
     * @param polylines polylines to remove
     * @return this multi-polyline
     */
    @NonNull
    public MultiPolyline removePolyline(@NonNull Polyline @NonNull ... polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        for (Polyline polyline : polylines) {
            removePolyline(polyline);
        }
        return this;
    }

    /**
     * Remove polylines from this multi-polyline.
     *
     * @param polylines polylines to remove
     * @return this multi-polyline
     */
    @NonNull
    public MultiPolyline removePolyline(@NonNull Collection<Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        this.polylines.removeAll(polylines);
        return this;
    }

    @Override
    @NonNull
    public JsonElement toJson() {
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
        MultiPolyline other = (MultiPolyline) o;
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
    @NonNull
    public String toString() {
        return "MultiPolyline{"
                + "key=" + getKey()
                + ",polylines=" + getPolylines()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
