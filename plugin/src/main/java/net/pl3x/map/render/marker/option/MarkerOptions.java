package net.pl3x.map.render.marker.option;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.render.marker.data.JsonSerializable;
import net.pl3x.map.render.marker.data.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Marker options.
 */
public class MarkerOptions implements JsonSerializable {
    private Stroke stroke;
    private Fill fill;
    private Tooltip tooltip;

    /**
     * Create empty marker options.
     */
    public MarkerOptions() {
    }

    /**
     * Create marker options.
     *
     * @param stroke  stroke rules
     * @param fill    fill rules
     * @param tooltip tooltip rules
     */
    public MarkerOptions(@Nullable Stroke stroke, @Nullable Fill fill, @Nullable Tooltip tooltip) {
        setStroke(stroke);
        setFill(fill);
        setTooltip(tooltip);
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
    public MarkerOptions setStroke(@Nullable Stroke stroke) {
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
    public MarkerOptions setFill(@Nullable Fill fill) {
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
    public MarkerOptions setTooltip(@Nullable Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray json = new JsonArray();
        json.add(getStroke() == null ? new JsonArray() : getStroke().toJson());
        json.add(getFill() == null ? new JsonArray() : getFill().toJson());
        json.add(getTooltip() == null ? new JsonArray() : getTooltip().toJson());
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
        MarkerOptions other = (MarkerOptions) o;
        return Objects.equals(getStroke(), other.getStroke())
                && Objects.equals(getFill(), other.getFill())
                && Objects.equals(getTooltip(), other.getTooltip());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStroke(), getFill(), getTooltip());
    }

    /**
     * Builder for {@link MarkerOptions}
     */
    public static class Builder {
        protected Stroke stroke = null;
        protected Fill fill = null;
        protected Tooltip tooltip = null;

        /**
         * Set the stroke weight.
         * <p>
         * Creates a new {@link Stroke} if none exists.
         *
         * @param weight stroke weight
         * @return this builder
         */
        @NotNull
        public Builder setStrokeWeight(int weight) {
            if (this.stroke == null) {
                this.stroke = new Stroke();
            }
            this.stroke.setWeight(weight);
            return this;
        }

        /**
         * Set the stroke color.
         * <p>
         * Creates a new {@link Stroke} if none exists.
         *
         * @param color argb color
         * @return this builder
         */
        @NotNull
        public Builder setStrokeColor(int color) {
            if (this.stroke == null) {
                this.stroke = new Stroke();
            }
            this.stroke.setColor(color);
            return this;
        }

        /**
         * Set the fill type.
         * <p>
         * Creates a new {@link Fill} if none exists.
         *
         * @param type fill type
         * @return this builder
         * @see <a href="https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/fill-rule">MDN fill-rule</a>
         */
        @NotNull
        public Builder setFillType(@NotNull Fill.Type type) {
            if (this.fill == null) {
                this.fill = new Fill();
            }
            this.fill.setType(type);
            return this;
        }

        /**
         * Set the fill color.
         * <p>
         * Creates a new {@link Fill} if none exists.
         *
         * @param color argb color
         * @return this builder
         */
        @NotNull
        public Builder setFillColor(int color) {
            if (this.fill == null) {
                this.fill = new Fill();
            }
            this.fill.setColor(color);
            return this;
        }

        /**
         * Set the tooltip type.
         * <p>
         * Creates a new {@link Tooltip} if none exists.
         *
         * @param type tooltip type
         * @return this builder
         */
        @NotNull
        public Builder setTooltipType(@NotNull Tooltip.Type type) {
            if (this.tooltip == null) {
                this.tooltip = new Tooltip();
            }
            this.tooltip.setType(type);
            return this;
        }

        /**
         * Set the tooltip string.
         * <p>
         * Creates a new {@link Tooltip} if none exists.
         *
         * @param string tooltip string
         * @return this builder
         */
        @NotNull
        public Builder setTooltipString(@NotNull String string) {
            if (this.tooltip == null) {
                this.tooltip = new Tooltip();
            }
            this.tooltip.setString(string);
            return this;
        }

        /**
         * Set the tooltip offset.
         * <p>
         * Creates a new {@link Tooltip} if none exists.
         *
         * @param offset tooltip offset
         * @return this builder
         */
        @NotNull
        public Builder setTooltipOffset(@NotNull Point offset) {
            if (this.tooltip == null) {
                this.tooltip = new Tooltip();
            }
            this.tooltip.setOffset(offset);
            return this;
        }

        /**
         * Create a new {@link MarkerOptions} instance from this builder's current state.
         *
         * @return new marker options instance
         */
        @NotNull
        public MarkerOptions build() {
            return new MarkerOptions(this.stroke, this.fill, this.tooltip);
        }
    }
}
