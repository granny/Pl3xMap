package net.pl3x.map.markers.option;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.JsonArrayWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Fill properties of a marker.
 */
public class Fill extends Option {
    private Boolean enabled;
    private Type type;
    private Integer color;

    /**
     * Create a fill rule with default options.
     */
    public Fill() {
    }

    /**
     * Create a fill rule with default enabled state.
     */
    public Fill(boolean enabled) {
        setEnabled(enabled);
    }

    /**
     * Create a fill rule.
     *
     * @param color argb color
     */
    public Fill(int color) {
        setColor(color);
    }

    /**
     * Whether to fill the path with color.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @return true if fill is enabled
     */
    @Nullable
    public Boolean isEnabled() {
        return this.enabled;
    }

    /**
     * Set whether to fill the path with color.
     * <p>
     * Setting to false will disable filling on polygons or circles.
     * <p>
     * Defaults to '<code>true</code>' if null.
     *
     * @param enabled whether fill is enabled
     * @return this fill rule
     */
    @NotNull
    public Fill setEnabled(@Nullable Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    /**
     * Get the fill type of this fill rule.
     * <p>
     * Defaults to '<code>{@link Type#EVENODD}</code>' if null.
     *
     * @return fill type
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
     */
    @Nullable
    public Type getType() {
        return this.type;
    }

    /**
     * Set a new fill type for this fill rule.
     * <p>
     * Defaults to '<code>{@link Type#EVENODD}</code>' if null.
     *
     * @param type new fill type
     * @return this fill rule
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
     */
    @NotNull
    public Fill setType(@Nullable Type type) {
        this.type = type;
        return this;
    }

    /**
     * Get the fill color of this fill rule.
     * <p>
     * Defaults to '<code>{@link Stroke#getColor()}</code>' if null.
     *
     * @return argb color
     */
    @Nullable
    public Integer getColor() {
        return this.color;
    }

    /**
     * Set a new color for this fill rule.
     * <p>
     * Defaults to '<code>{@link Stroke#getColor()}</code>' if null.
     *
     * @param color new argb color
     * @return this fill rule
     */
    @NotNull
    public Fill setColor(@Nullable Integer color) {
        this.color = color;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(isEnabled());
        wrapper.add(getType());
        wrapper.add(getColor());
        return wrapper.getJsonArray();
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
        return Objects.equals(isEnabled(), other.isEnabled())
                && Objects.equals(getType(), other.getType())
                && Objects.equals(getColor(), other.getColor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(isEnabled(), getType(), getColor());
    }

    @Override
    public String toString() {
        return "Fill{enabled=" + isEnabled() + ",type=" + getType() + ",color=" + getColor() + "}";
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
