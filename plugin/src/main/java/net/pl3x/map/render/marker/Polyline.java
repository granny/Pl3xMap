package net.pl3x.map.render.marker;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

/**
 * Polyline marker.
 */
public class Polyline extends Marker {
    public Polyline() {
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
        Polyline other = (Polyline) o;
        return getOptions().equals(other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }
}
