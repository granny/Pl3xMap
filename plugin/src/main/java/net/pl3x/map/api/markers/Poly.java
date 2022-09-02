package net.pl3x.map.api.markers;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import net.pl3x.map.api.JsonSerializable;
import net.pl3x.map.api.markers.marker.Polygon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a polygon in a {@link Polygon}.
 * <p>
 * A polygon requires at least one {@link Ring} to represent
 * the outer polygon shape. Any additional rings will be used
 * to cut out "holes" in the outer polygon.
 */
public class Poly implements JsonSerializable {
    private final Collection<Ring> rings = new ArrayList<>();

    public Poly(@NotNull Ring ring) {
        addRing(ring);
    }

    public Poly(@NotNull Ring @NotNull ... rings) {
        addRing(rings);
    }

    public Poly(@NotNull Collection<Ring> rings) {
        addRing(rings);
    }

    public static Poly of(@NotNull Ring ring) {
        return new Poly(ring);
    }

    public static Poly of(@NotNull Ring @NotNull ... rings) {
        return new Poly(rings);
    }

    public static Poly of(@NotNull Collection<Ring> rings) {
        return new Poly(rings);
    }

    @NotNull
    public Collection<Ring> getRings() {
        return this.rings;
    }

    @NotNull
    public Poly clearRings() {
        this.rings.clear();
        return this;
    }

    @NotNull
    public Poly addRing(@NotNull Ring ring) {
        Preconditions.checkNotNull(ring, "Poly ring is null");
        this.rings.add(ring);
        return this;
    }

    @NotNull
    public Poly addRing(@NotNull Ring @NotNull ... rings) {
        Preconditions.checkNotNull(rings, "Poly rings is null");
        addRing(Arrays.asList(rings));
        return this;
    }

    @NotNull
    public Poly addRing(@NotNull Collection<Ring> rings) {
        Preconditions.checkNotNull(rings, "Poly rings is null");
        this.rings.addAll(rings);
        return this;
    }

    @NotNull
    public Poly removeRing(@NotNull Ring ring) {
        Preconditions.checkNotNull(ring, "Poly ring is null");
        this.rings.remove(ring);
        return this;
    }

    @NotNull
    public Poly removeRing(@NotNull Ring @NotNull ... rings) {
        Preconditions.checkNotNull(rings, "Poly rings is null");
        removeRing(Arrays.asList(rings));
        return this;
    }

    @NotNull
    public Poly removeRing(@NotNull Collection<Ring> rings) {
        Preconditions.checkNotNull(rings, "Poly rings is null");
        this.rings.removeAll(rings);
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        getRings().forEach(ring -> json.add(ring.toJson()));
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
        Poly other = (Poly) o;
        return Objects.equals(getRings(), other.getRings());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRings());
    }

    @Override
    public String toString() {
        return "Poly{rings=" + getRings() + "}";
    }
}
