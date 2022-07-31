package net.pl3x.map.render.marker.option;

import com.google.common.base.Preconditions;
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MarkerOptions {
    private Stroke stroke;
    private Fill fill;
    private Tooltip tooltip;

    public MarkerOptions() {
        this(new Stroke(), new Fill(), new Tooltip());
    }

    public MarkerOptions(@NotNull Stroke stroke, @NotNull Fill fill, @NotNull Tooltip tooltip) {
        setStroke(stroke);
        setFill(fill);
        setTooltip(tooltip);
    }

    @NotNull
    public Stroke getStroke() {
        return this.stroke;
    }

    @NotNull
    public MarkerOptions setStroke(@NotNull Stroke stroke) {
        Preconditions.checkNotNull(stroke);
        this.stroke = stroke;
        return this;
    }

    @NotNull
    public Fill getFill() {
        return this.fill;
    }

    @NotNull
    public MarkerOptions setFill(@NotNull Fill fill) {
        Preconditions.checkNotNull(fill);
        this.fill = fill;
        return this;
    }

    @NotNull
    public Tooltip getTooltip() {
        return this.tooltip;
    }

    @NotNull
    public MarkerOptions setTooltip(@NotNull Tooltip tooltip) {
        Preconditions.checkNotNull(tooltip);
        this.tooltip = tooltip;
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
        MarkerOptions other = (MarkerOptions) o;
        return getStroke().equals(other.getStroke())
                && getFill().equals(other.getFill())
                && getTooltip().equals(other.getTooltip());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStroke(), getFill(), getTooltip());
    }
}
