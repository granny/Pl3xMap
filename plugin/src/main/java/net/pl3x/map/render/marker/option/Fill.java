package net.pl3x.map.render.marker.option;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fill properties of a marker.
 */
public class Fill {
    private Type type;
    private int color;

    public Fill() {
        this(Type.EVENODD, 0x33FF0000);
    }

    public Fill(@NotNull Type type, int color) {
        setType(type);
        setColor(color);
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    @NotNull
    public Fill setType(@NotNull Type type) {
        Preconditions.checkNotNull(type);
        this.type = type;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    @NotNull
    public Fill setColor(int color) {
        this.color = color;
        return this;
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
        Fill other = (Fill) o;
        return getType().equals(other.getType())
                && getColor() == other.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getColor());
    }

    public enum Type {
        NONZERO, EVENODD
    }
}
