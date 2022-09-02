package net.pl3x.map.api.markers.option;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.JsonSerializable;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.util.Mathf;
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
     * Get the content of this tooltip rule.
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
     * Defaults to '<code>tooltipPane</code>' if null.
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
     * Defaults to '<code>tooltipPane</code>' if null.
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
     * <p>
     * Defaults to '<code>{@link Point#ZERO}</code>' if null.
     *
     * @return tooltip offset from marker point
     */
    @Nullable
    public Point getOffset() {
        return this.offset;
    }

    /**
     * Set offset of this tooltip rule from marker point.
     * <p>
     * Defaults to '<code>{@link Point#ZERO}</code>' if null.
     *
     * @param offset tooltip offset
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setOffset(@Nullable Point offset) {
        this.offset = offset;
        return this;
    }

    /**
     * Get the direction where to open the tooltip.
     * <p>
     * Defaults to '<code>{@link Direction#AUTO}</code>' if null.
     *
     * @return opening direction
     */
    @Nullable
    public Direction getDirection() {
        return this.direction;
    }

    /**
     * Set the direction where to open the tooltip.
     * <p>
     * Defaults to '<code>{@link Direction#AUTO}</code>' if null.
     *
     * @param direction opening direction
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setDirection(@Nullable Direction direction) {
        this.direction = direction;
        return this;
    }

    /**
     * Get whether to open the tooltip permanently or only on mouseover.
     * <p>
     * Defaults to '<code>false</code>' if null.
     *
     * @return true if opened permanently
     */
    @Nullable
    public Boolean isPermanent() {
        return this.permanent;
    }

    /**
     * Set whether to open the tooltip permanently or only on mouseover
     * <p>
     * Defaults to '<code>false</code>' if null.
     *
     * @param permanent opened permanently
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setPermanent(@Nullable Boolean permanent) {
        this.permanent = permanent;
        return this;
    }

    /**
     * Get whether the tooltip is sticky or not.
     * <p>
     * A sticky tooltip will stick to and follow the mouse instead of the anchor.
     * <p>
     * Defaults to '<code>false</code>' if null.
     *
     * @return sticky state
     */
    @Nullable
    public Boolean isSticky() {
        return this.sticky;
    }

    /**
     * Set whether the tooltip is sticky or not.
     * <p>
     * A sticky tooltip will stick to and follow the mouse instead of the anchor.
     * <p>
     * Defaults to '<code>false</code>' if null.
     *
     * @param sticky sticky state
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setSticky(@Nullable Boolean sticky) {
        this.sticky = sticky;
        return this;
    }

    /**
     * Get the tooltip opacity percent.
     * <p>
     * Defaults to '<code>0.9D</code>' if null.
     *
     * @return tooltip opacity
     */
    @Nullable
    public Double getOpacity() {
        return this.opacity;
    }

    /**
     * Set the tooltip opacity percent.
     * <p>
     * Defaults to '<code>0.9D</code>' if null.
     *
     * @param opacity tooltip opacity
     * @return this tooltip rule
     */
    @NotNull
    public Tooltip setOpacity(@Nullable Double opacity) {
        this.opacity = opacity == null ? null : Mathf.clamp(0D, 1D, opacity);
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(getContent());
        json.add(getPane());
        json.add(vec(getOffset()));
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
