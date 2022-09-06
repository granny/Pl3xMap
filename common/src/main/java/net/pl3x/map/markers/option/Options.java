package net.pl3x.map.markers.option;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.JsonArrayWrapper;
import net.pl3x.map.JsonSerializable;
import net.pl3x.map.markers.marker.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a {@link Marker}'s options.
 */
public class Options implements JsonSerializable {
    private Stroke stroke;
    private Fill fill;
    private Tooltip tooltip;
    private Popup popup;

    /**
     * Create empty marker options.
     */
    public Options() {
    }

    /**
     * Create marker options.
     *
     * @param stroke  stroke rules
     * @param fill    fill rules
     * @param tooltip tooltip rules
     */
    public Options(@Nullable Stroke stroke, @Nullable Fill fill, @Nullable Tooltip tooltip, @Nullable Popup popup) {
        setStroke(stroke);
        setFill(fill);
        setTooltip(tooltip);
        setPopup(popup);
    }

    /**
     * Get stroke rules.
     *
     * @return stroke rules
     */
    @Nullable
    public Stroke getStroke() {
        return this.stroke;
    }

    /**
     * Set new stroke rules.
     *
     * @param stroke new stroke rules.
     * @return this marker options
     */
    @NotNull
    public Options setStroke(@Nullable Stroke stroke) {
        this.stroke = stroke;
        return this;
    }

    /**
     * Get fill rules.
     *
     * @return fill rules
     */
    @Nullable
    public Fill getFill() {
        return this.fill;
    }

    /**
     * Set new fill rules.
     *
     * @param fill new fill rules
     * @return this marker options
     */
    @NotNull
    public Options setFill(@Nullable Fill fill) {
        this.fill = fill;
        return this;
    }

    /**
     * Get tooltip rules.
     *
     * @return tooltip rules
     */
    @Nullable
    public Tooltip getTooltip() {
        return this.tooltip;
    }

    /**
     * Set new tooltip rules.
     *
     * @param tooltip new tooltip rules
     * @return this marker options
     */
    @NotNull
    public Options setTooltip(@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    /**
     * Get popup rules.
     *
     * @return popup rules
     */
    @Nullable
    public Popup getPopup() {
        return this.popup;
    }

    /**
     * Set new popup rules.
     *
     * @param popup new popup rules
     * @return this marker options
     */
    @NotNull
    public Options setPopup(@Nullable Popup popup) {
        this.popup = popup;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(getStroke());
        wrapper.add(getFill());
        wrapper.add(getPopup());
        wrapper.add(getTooltip());
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
        Options other = (Options) o;
        return Objects.equals(getStroke(), other.getStroke())
                && Objects.equals(getFill(), other.getFill())
                && Objects.equals(getTooltip(), other.getTooltip())
                && Objects.equals(getPopup(), other.getPopup());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStroke(), getFill(), getTooltip(), getPopup());
    }

    @Override
    public String toString() {
        return "Options{fill=" + getFill() + ",stroke=" + getStroke() + ",tooltip=" + getTooltip() + ",popup=" + getPopup() + "}";
    }
}
