package net.pl3x.map.api.marker.option;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.JsonSerializable;
import net.pl3x.map.api.marker.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Tooltip properties of a marker.
 */
public class Tooltip implements JsonSerializable {
    private Type type;
    private String string;
    private Point offset;

    /**
     * Create a tooltip rule with default options.
     */
    public Tooltip() {
        this(Type.HOVER, "", Point.ZERO);
    }

    /**
     * Create a tooltip rule.
     *
     * @param type   tooltip type
     * @param string tooltip string
     * @param offset offset from marker point
     */
    public Tooltip(@NotNull Type type, @NotNull String string, @NotNull Point offset) {
        setType(type);
        setString(string);
        setOffset(offset);
    }

    /**
     * Get type of this tooltip rule.
     *
     * @return tooltip type
     */
    @NotNull
    public Type getType() {
        return this.type;
    }

    /**
     * Set new type for this tooltip rule.
     *
     * @param type tooltip type
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setType(@NotNull Type type) {
        Preconditions.checkNotNull(type);
        this.type = type;
        return this;
    }

    /**
     * Get the string of this tooltip rule.
     *
     * @return tooltip string
     */
    @NotNull
    public String getString() {
        return this.string;
    }

    /**
     * Set the string for this tooltip rule.
     * <p>
     * HTML is valid here.
     *
     * @param string tooltip string
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setString(@NotNull String string) {
        Preconditions.checkNotNull(string);
        this.string = string;
        return this;
    }

    /**
     * Get offset of this tooltip rule.
     *
     * @return tooltip offset from marker point
     */
    @NotNull
    public Point getOffset() {
        return this.offset;
    }

    /**
     * Set offset of this tooltip rule from marker point
     *
     * @param offset tooltip offset
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setOffset(@NotNull Point offset) {
        Preconditions.checkNotNull(offset);
        this.offset = offset;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(getType().ordinal());
        json.add(getString());
        json.add(getOffset().toJson());
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
        Tooltip other = (Tooltip) o;
        return getType().equals(other.getType())
                && getString().equals(other.getString())
                && getOffset().equals(other.getOffset());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getString(), getOffset());
    }

    @Override
    public String toString() {
        return "Tooltip{type=" + getType() + ",string=" + getString() + ",offset=" + getOffset() + "}";
    }

    /**
     * Tooltip type.
     */
    public enum Type {
        /**
         * Make tooltip appear when marker is clicked.
         */
        CLICK,
        /**
         * Make tooltip appear when marker is hovered.
         */
        HOVER
    }
}
