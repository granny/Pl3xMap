package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.JsonObjectWrapper;
import net.pl3x.map.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a multi-polyline marker.
 */
public class MultiPolyline extends Marker<MultiPolyline> {
    private final List<Polyline> polylines = new ArrayList<>();

    private MultiPolyline(@NotNull Key key) {
        super("multiline", key);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key      identifying key
     * @param polyline polyline to add
     */
    public MultiPolyline(@NotNull Key key, @NotNull Polyline polyline) {
        this(key);
        addPolyline(polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public MultiPolyline(@NotNull Key key, @NotNull Polyline @NotNull ... polylines) {
        this(key);
        addPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     */
    public MultiPolyline(@NotNull Key key, @NotNull Collection<Polyline> polylines) {
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
    public static MultiPolyline of(@NotNull Key key, @NotNull Polyline polyline) {
        return new MultiPolyline(key, polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline of(@NotNull Key key, @NotNull Polyline @NotNull ... polylines) {
        return new MultiPolyline(key, polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param key       identifying key
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline of(@NotNull Key key, @NotNull Collection<Polyline> polylines) {
        return new MultiPolyline(key, polylines);
    }

    /**
     * Get the list of polylines in this multi-polyline.
     *
     * @return list of polylines
     */
    @NotNull
    public List<Polyline> getPolylines() {
        return this.polylines;
    }

    /**
     * Clear the list of polylines in this multi-polyline.
     *
     * @return this multi-polyline
     */
    @NotNull
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
    @NotNull
    public MultiPolyline addPolyline(@NotNull Polyline polyline) {
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
    @NotNull
    public MultiPolyline addPolyline(@NotNull Polyline @NotNull ... polylines) {
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
    @NotNull
    public MultiPolyline addPolyline(@NotNull Collection<Polyline> polylines) {
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
    @NotNull
    public MultiPolyline removePolyline(@NotNull Polyline polyline) {
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
    @NotNull
    public MultiPolyline removePolyline(@NotNull Polyline @NotNull ... polylines) {
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
    @NotNull
    public MultiPolyline removePolyline(@NotNull Collection<Polyline> polylines) {
        Preconditions.checkNotNull(polylines, "MultiPolyline polylines is null");
        this.polylines.removeAll(polylines);
        return this;
    }

    @Override
    @NotNull
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
    public String toString() {
        return "MultiPolyline{"
                + "key=" + getKey()
                + ",polylines=" + getPolylines()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
