package net.pl3x.map.render.marker;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

/**
 * Polygon marker.
 */
public class Polygon extends Marker {
    public Polygon() {
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
        return getOptions().equals(other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }
}
