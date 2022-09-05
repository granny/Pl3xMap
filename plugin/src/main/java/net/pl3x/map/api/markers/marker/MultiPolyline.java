package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.api.JsonArrayWrapper;
import net.pl3x.map.api.markers.option.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a multi-polyline marker.
 */
public class MultiPolyline extends Marker {
    private final List<Polyline> polylines = new ArrayList<>();

    private MultiPolyline() {
        super("multiline");
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polyline polyline to add
     */
    public MultiPolyline(@NotNull Polyline polyline) {
        this();
        addPolyline(polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polylines polylines to add
     */
    public MultiPolyline(@NotNull Polyline @NotNull ... polylines) {
        this();
        addPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polylines polylines to add
     */
    public MultiPolyline(@NotNull Collection<Polyline> polylines) {
        this();
        addPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polyline polyline to add
     * @return a new multi-polyline
     */
    public static MultiPolyline of(@NotNull Polyline polyline) {
        return new MultiPolyline(polyline);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline of(@NotNull Polyline @NotNull ... polylines) {
        return new MultiPolyline(polylines);
    }

    /**
     * Create a new multi-polyline.
     *
     * @param polylines polylines to add
     * @return a new multi-polyline
     */
    public static MultiPolyline of(@NotNull Collection<Polyline> polylines) {
        return new MultiPolyline(polylines);
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
    public MultiPolyline setOptions(@Nullable Options options) {
        return (MultiPolyline) super.setOptions(options);
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
        MultiPolyline other = (MultiPolyline) o;
        return Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }

    @Override
    public String toString() {
        return "MultiPolyline{polylines=" + getPolylines() + ",options=" + getOptions() + "}";
    }
}
