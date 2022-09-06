package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import net.pl3x.map.JsonArrayWrapper;
import net.pl3x.map.markers.option.Options;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a multi-polygon marker.
 */
public class MultiPolygon extends Marker {
    private final List<Polygon> polygons = new ArrayList<>();

    private MultiPolygon() {
        super("multipoly");
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygon polygon to add
     */
    public MultiPolygon(@NotNull Polygon polygon) {
        this();
        addPolygon(polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygons polygons to add
     */
    public MultiPolygon(@NotNull Polygon @NotNull ... polygons) {
        this();
        addPolygon(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygons polygons to add
     */
    public MultiPolygon(@NotNull Collection<Polygon> polygons) {
        this();
        addPolygon(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygon polygon to add
     * @return a new multi-polygon
     */
    public static MultiPolygon of(@NotNull Polygon polygon) {
        return new MultiPolygon(polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon of(@NotNull Polygon @NotNull ... polygons) {
        return new MultiPolygon(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon of(@NotNull Collection<Polygon> polygons) {
        return new MultiPolygon(polygons);
    }

    /**
     * Get the list of polygons in this multi-polygon.
     *
     * @return list of polygons
     */
    @NotNull
    public List<Polygon> getPolygons() {
        return this.polygons;
    }

    /**
     * Clear the list of polygons in this multi-polygon.
     *
     * @return this multi-polygon
     */
    @NotNull
    public MultiPolygon clearPolygons() {
        this.polygons.clear();
        return this;
    }

    /**
     * Add a polygon to this multi-polygon.
     *
     * @param polygon polygon to add
     * @return this multi-polygon
     */
    @NotNull
    public MultiPolygon addPolygon(@NotNull Polygon polygon) {
        Preconditions.checkNotNull(polygon, "MultiPolygon polygon is null");
        this.polygons.add(polygon);
        return this;
    }

    /**
     * Add polygons to this multi-polygon.
     *
     * @param polygons polygons to add
     * @return this multi-polygon
     */
    @NotNull
    public MultiPolygon addPolygon(@NotNull Polygon @NotNull ... polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        for (Polygon polygon : polygons) {
            addPolygon(polygon);
        }
        return this;
    }

    /**
     * Add polygons to this multi-polygon.
     *
     * @param polygons polygons to add
     * @return this multi-polygon
     */
    @NotNull
    public MultiPolygon addPolygon(@NotNull Collection<Polygon> polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        this.polygons.addAll(polygons);
        return this;
    }

    /**
     * Remove a polygon from this multi-polygon.
     *
     * @param polygon polygon to remove
     * @return this multi-polygon
     */
    @NotNull
    public MultiPolygon removePoly(@NotNull Polygon polygon) {
        Preconditions.checkNotNull(polygon, "MultiPolygon polygon is null");
        this.polygons.remove(polygon);
        return this;
    }

    /**
     * Remove polygons from this multi-polygon.
     *
     * @param polygons polygons to remove
     * @return this multi-polygon
     */
    @NotNull
    public MultiPolygon removePoly(@NotNull Polygon @NotNull ... polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        for (Polygon polygon : polygons) {
            removePoly(polygon);
        }
        return this;
    }

    /**
     * Remove polygons from this multi-polygon.
     *
     * @param polygons polygons to remove
     * @return this multi-polygon
     */
    @NotNull
    public MultiPolygon removePoly(@NotNull Collection<Polygon> polygons) {
        Preconditions.checkNotNull(polygons, "MultiPolygon polygons is null");
        this.polygons.removeAll(polygons);
        return this;
    }

    @Override
    @NotNull
    public MultiPolygon setOptions(@Nullable Options options) {
        return (MultiPolygon) super.setOptions(options);
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        getPolygons().forEach(wrapper::add);
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
        MultiPolygon other = (MultiPolygon) o;
        return Objects.equals(getPolygons(), other.getPolygons())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }

    @Override
    public String toString() {
        return "MultiPolygon{polygons=" + getPolygons() + ",options=" + getOptions() + "}";
    }
}
