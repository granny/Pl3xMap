package net.pl3x.map.render.marker;

import java.util.Objects;
import org.jetbrains.annotations.Nullable;

/**
 * Rectangle marker.
 */
public class Rectangle extends Marker {
    public Rectangle() {
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
        Rectangle other = (Rectangle) o;
        return getOptions().equals(other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions());
    }
}
