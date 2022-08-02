package net.pl3x.map.render.marker.option;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.render.marker.data.JsonSerializable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fill properties of a marker.
 */
public class Fill implements JsonSerializable {
    private Type type;
    private int color;

    /**
     * Create a fill rule with default options.
     */
    public Fill() {
        this(Type.EVENODD, 0x33FF0000);
    }

    /**
     * Create a fill rule.
     *
     * @param type  fill type
     * @param color argb color
     */
    public Fill(@NotNull Type type, int color) {
        setType(type);
        setColor(color);
    }

    /**
     * Get the fill type of this fill rule.
     *
     * @return fill type
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
     */
    @NotNull
    public Type getType() {
        return this.type;
    }

    /**
     * Set a new fill type for this fill rule.
     *
     * @param type new fill type
     * @return this fill rule
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
     */
    @NotNull
    public Fill setType(@NotNull Type type) {
        Preconditions.checkNotNull(type);
        this.type = type;
        return this;
    }

    /**
     * Get the fill color of this fill rule.
     *
     * @return argb color
     */
    public int getColor() {
        return this.color;
    }

    /**
     * Set a new color for this fill rule.
     *
     * @param color new argb color
     * @return this fill rule
     */
    @NotNull
    public Fill setColor(int color) {
        this.color = color;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(getType().ordinal());
        json.add(getColor());
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
        Fill other = (Fill) o;
        return getType().equals(other.getType())
                && getColor() == other.getColor();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getType(), getColor());
    }

    /**
     * Fill type.
     *
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
     */
    public enum Type {
        /**
         * The value nonzero determines the "insideness" of a point
         * in the shape by drawing a ray from that point to infinity
         * in any direction, and then examining the places where a
         * segment of the shape crosses the ray. Starting with a
         * count of zero, add one each time a path segment crosses
         * the ray from left to right and subtract one each time a
         * path segment crosses the ray from right to left. After
         * counting the crossings, if the result is zero then the
         * point is outside the path. Otherwise, it is inside.
         *
         * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
         */
        NONZERO,
        /**
         * The value evenodd determines the "insideness" of a point
         * in the shape by drawing a ray from that point to infinity
         * in any direction and counting the number of path segments
         * from the given shape that the ray crosses. If this number
         * is odd, the point is inside; if even, the point is outside.
         *
         * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
         */
        EVENODD
    }
}
