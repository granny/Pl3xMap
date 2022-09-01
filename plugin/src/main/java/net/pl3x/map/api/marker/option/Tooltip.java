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
    private String content;
    private String pane;
    private Point offset;
    private Direction direction;
    private Boolean permanent;
    private Boolean sticky;
    private Double opacity;

    /**
     * Create a tooltip rule.
     *
     * @param string tooltip content
     */
    public Tooltip(@NotNull String string) {
        setContent(string);
    }

    /**
     * Get the string of this tooltip rule.
     *
     * @return tooltip content
     */
    @NotNull
    public String getContent() {
        return this.content;
    }

    /**
     * Set the content for this tooltip rule.
     * <p>
     * HTML is valid here.
     *
     * @param content tooltip content
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setContent(@NotNull String content) {
        Preconditions.checkNotNull(content, "Tooltip content is null");
        this.content = content;
        return this;
    }

    /**
     * Get the map pane where the tooltip will be added.
     * <p>
     * Defaults to 'tooltipPane' if null.
     *
     * @return map pane
     */
    @Nullable
    public String getPane() {
        return this.pane;
    }

    /**
     * Set the map pane where the tooltip will be added.
     * <p>
     * Defaults to 'tooltipPane' if null.
     *
     * @param pane map pane
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setPane(@Nullable String pane) {
        this.pane = pane;
        return this;
    }

    /**
     * Get offset of this tooltip rule.
     *
     * @return tooltip offset from marker point
     */
    @Nullable
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
    public Tooltip setOffset(@Nullable Point offset) {
        this.offset = offset;
        return this;
    }

    @Nullable
    public Direction getDirection() {
        return this.direction;
    }

    @NotNull
    public Tooltip setDirection(@Nullable Direction direction) {
        this.direction = direction;
        return this;
    }

    @Nullable
    public Boolean isPermanent() {
        return this.permanent;
    }

    @NotNull
    public Tooltip setPermanent(@Nullable Boolean permanent) {
        this.permanent = permanent;
        return this;
    }

    @Nullable
    public Boolean isSticky() {
        return this.sticky;
    }

    @NotNull
    public Tooltip setSticky(@Nullable Boolean sticky) {
        this.sticky = sticky;
        return this;
    }

    @Nullable
    public Double getOpacity() {
        return this.opacity;
    }

    @NotNull
    public Tooltip setOpacity(@Nullable Double opacity) {
        this.opacity = opacity;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(getContent());
        json.add(getPane());
        json.add(point(getOffset()));
        json.add(enumeration(getDirection()));
        json.add(bool(isPermanent()));
        json.add(bool(isSticky()));
        json.add(getOpacity());
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
        return getContent().equals(other.getContent())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOffset(), other.getOffset())
                && Objects.equals(getDirection(), other.getDirection())
                && Objects.equals(isPermanent(), other.isPermanent())
                && Objects.equals(isSticky(), other.isSticky())
                && Objects.equals(getOpacity(), other.getOpacity());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getPane(), getOffset(), getDirection(), isPermanent(), isSticky(), getOpacity());
    }

    @Override
    public String toString() {
        return "Tooltip{"
                + ",content=" + getContent()
                + ",pane=" + getPane()
                + ",offset=" + getOffset()
                + ",direction=" + getDirection()
                + ",permanent=" + isPermanent()
                + ",sticky=" + isSticky()
                + ",opacity=" + getOpacity()
                + "}";
    }

    /**
     * Represents the direction where to open the tooltip.
     */
    public enum Direction {
        /**
         * Opens the tooltip to the right of the anchor.
         */
        RIGHT,
        /**
         * Opens the tooltip to the left of the anchor.
         */
        LEFT,
        /**
         * Opens the tooltip above the anchor.
         */
        TOP,
        /**
         * Opens the tooltip below the anchor.
         */
        BOTTOM,
        /**
         * Opens the tooltip centered on the anchor.
         */
        CENTER,
        /**
         * Opens the tooltip either to the right or left according
         * to the tooltip position on the map.
         */
        AUTO
    }
}
