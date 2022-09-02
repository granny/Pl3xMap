package net.pl3x.map.api.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import net.pl3x.map.api.markers.Poly;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a polygon marker.
 */
public class Polygon extends Marker {
    private final Collection<Poly> polys = new ArrayList<>();

    private Polygon() {
        super("poly");
    }

    public Polygon(@NotNull Poly poly) {
        this();
        addPoly(poly);
    }

    public Polygon(@NotNull Poly @NotNull ... polys) {
        this();
        addPoly(polys);
    }

    public Polygon(@NotNull Collection<Poly> polys) {
        this();
        addPoly(polys);
    }

    public static Polygon of(@NotNull Poly poly) {
        return new Polygon(poly);
    }

    public static Polygon of(@NotNull Poly @NotNull ... polys) {
        return new Polygon(polys);
    }

    public static Polygon of(@NotNull Collection<Poly> polys) {
        return new Polygon(polys);
    }

    @NotNull
    public Collection<Poly> getPolys() {
        return this.polys;
    }

    @NotNull
    public Polygon clearPolys() {
        this.polys.clear();
        return this;
    }

    @NotNull
    public Polygon addPoly(@NotNull Poly poly) {
        Preconditions.checkNotNull(poly, "Polygon poly is null");
        this.polys.add(poly);
        return this;
    }

    @NotNull
    public Polygon addPoly(@NotNull Poly @NotNull ... polys) {
        Preconditions.checkNotNull(polys, "Polygon polys is null");
        addPoly(Arrays.asList(polys));
        return this;
    }

    @NotNull
    public Polygon addPoly(@NotNull Collection<Poly> polys) {
        Preconditions.checkNotNull(polys, "Polygon polys is null");
        this.polys.addAll(polys);
        return this;
    }

    @NotNull
    public Polygon removePoly(@NotNull Poly poly) {
        Preconditions.checkNotNull(poly, "Polygon poly is null");
        this.polys.remove(poly);
        return this;
    }

    @NotNull
    public Polygon removePoly(@NotNull Poly @NotNull ... polys) {
        Preconditions.checkNotNull(polys, "Polygon polys is null");
        removePoly(Arrays.asList(polys));
        return this;
    }

    @NotNull
    public Polygon removePoly(@NotNull Collection<Poly> polys) {
        Preconditions.checkNotNull(polys, "Polygon polys is null");
        this.polys.removeAll(polys);
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        getPolys().forEach(poly -> json.add(poly.toJson()));
        return json;
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
        return Objects.equals(getPolys(), other.getPolys())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }

    @Override
    public String toString() {
        return "Polygon{polys=" + getPolys() + ",options=" + getOptions() + "}";
    }
}
