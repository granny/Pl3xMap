package net.pl3x.map.render.marker.option;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.pl3x.map.render.marker.data.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Tooltip {
    private Type type;
    private String string;
    private Point anchor;

    public Tooltip() {
        this(Type.HOVER, "", Point.ZERO);
    }

    public Tooltip(@NotNull Type type, @NotNull String string, @NotNull Point anchor) {
        setType(type);
        setString(string);
        setAnchor(anchor);
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    @NotNull
    public Tooltip setType(@NotNull Type type) {
        Preconditions.checkNotNull(type);
        this.type = type;
        return this;
    }

    @NotNull
    public String getString() {
        return this.string;
    }

    @NotNull
    public Tooltip setString(@NotNull String string) {
        Preconditions.checkNotNull(string);
        this.string = string;
        return this;
    }

    @NotNull
    public Point getAnchor() {
        return this.anchor;
    }

    @NotNull
    public Tooltip setAnchor(@NotNull Point anchor) {
        Preconditions.checkNotNull(anchor);
        this.anchor = anchor;
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
        Tooltip other = (Tooltip) o;
        return getType().equals(other.getType())
                && getString().equals(other.getString())
                && getAnchor().equals(other.getAnchor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getString(), getAnchor());
    }

    public enum Type {
        CLICK, HOVER
    }
}
