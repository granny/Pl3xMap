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
 * Represents a multi-polygon marker.
 */
public class MultiPolygon extends Marker<MultiPolygon> {
    private final List<Polygon> polygons = new ArrayList<>();

    private MultiPolygon(@NotNull Key key) {
        super("multipoly", key);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key     identifying key
     * @param polygon polygon to add
     */
    public MultiPolygon(@NotNull Key key, @NotNull Polygon polygon) {
        this(key);
        addPolygon(polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     */
    public MultiPolygon(@NotNull Key key, @NotNull Polygon @NotNull ... polygons) {
        this(key);
        addPolygon(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     */
    public MultiPolygon(@NotNull Key key, @NotNull Collection<Polygon> polygons) {
        this(key);
        addPolygon(polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key     identifying key
     * @param polygon polygon to add
     * @return a new multi-polygon
     */
    public static MultiPolygon of(@NotNull Key key, @NotNull Polygon polygon) {
        return new MultiPolygon(key, polygon);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon of(@NotNull Key key, @NotNull Polygon @NotNull ... polygons) {
        return new MultiPolygon(key, polygons);
    }

    /**
     * Create a new multi-polygon.
     *
     * @param key      identifying key
     * @param polygons polygons to add
     * @return a new multi-polygon
     */
    public static MultiPolygon of(@NotNull Key key, @NotNull Collection<Polygon> polygons) {
        return new MultiPolygon(key, polygons);
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
    public JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("polygons", getPolygons());
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
        MultiPolygon other = (MultiPolygon) o;
        return getKey().equals(other.getKey())
                && Objects.equals(getPolygons(), other.getPolygons())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getPolygons(), getPane(), getOptions());
    }

    @Override
    public String toString() {
        return "MultiPolygon{"
                + "key=" + getKey()
                + ",polygons=" + getPolygons()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
