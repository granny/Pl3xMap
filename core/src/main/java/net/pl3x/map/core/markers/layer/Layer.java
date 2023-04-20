package net.pl3x.map.core.markers.layer;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;
import net.pl3x.map.core.Keyed;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a layer of markers and other metadata.
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class Layer extends Keyed {
    private Supplier<String> labelSupplier;
    private int updateInterval = 15;
    private boolean showControls = true;
    private boolean defaultHidden = false;
    private int priority = 99;
    private Integer zIndex = 99;
    private String pane;
    private String css;

    /**
     * Create a layer.
     *
     * @param key key for layer
     */
    public Layer(@NonNull String key, @NonNull Supplier<String> labelSupplier) {
        super(key);
        this.labelSupplier = labelSupplier;
    }

    /**
     * Get the label of this layer, shown in the control box.
     *
     * @return layer label
     */
    @NonNull
    public String getLabel() {
        return this.labelSupplier.get();
    }

    /**
     * Set the label of this layer, shown in the control box.
     *
     * @param label new label
     * @return this layer
     */
    @NonNull
    public Layer setLabel(@NonNull String label) {
        Preconditions.checkNotNull(label, "Layer label is null");
        this.labelSupplier = () -> label;
        return this;
    }

    /**
     * Set the label supplier of this layer, shown in the control box.
     *
     * @param labelSupplier new label supplier
     * @return this layer
     */
    @NonNull
    public Layer setLabel(@NonNull Supplier<String> labelSupplier) {
        Preconditions.checkNotNull(labelSupplier, "Layer label supplier is null");
        this.labelSupplier = labelSupplier;
        return this;
    }

    /**
     * Get this layer's update interval (in seconds).
     *
     * @return update interval
     */
    public int getUpdateInterval() {
        return this.updateInterval;
    }

    /**
     * Set this layer's update interval (in seconds).
     *
     * @param updateInterval new update interval
     * @return this layer
     */
    @NonNull
    public Layer setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
        return this;
    }

    /**
     * Get whether to show this layer in the control box.
     *
     * @return true if showing controls
     */
    public boolean shouldShowControls() {
        return this.showControls;
    }

    /**
     * Set whether to show this layer in the control box.
     *
     * @param showControls true to show
     * @return this layer
     */
    @NonNull
    public Layer setShowControls(boolean showControls) {
        this.showControls = showControls;
        return this;
    }

    /**
     * Get if this layer is hidden by default in the control box.
     *
     * @return true if hidden by default
     */
    public boolean isDefaultHidden() {
        return this.defaultHidden;
    }

    /**
     * Set if this layer is hidden by default in the control box.
     *
     * @param defaultHidden true to hide by default
     * @return this layer
     */
    @NonNull
    public Layer setDefaultHidden(boolean defaultHidden) {
        this.defaultHidden = defaultHidden;
        return this;
    }

    /**
     * Get the indexed order for this layer in the control box.
     * <p>
     * Falls back to alphanumeric ordering based on label if there are order conflicts.
     *
     * @return layer priority
     */
    public int getPriority() {
        return this.priority;
    }

    /**
     * Set the indexed order for this layer in the control box.
     * <p>
     * Falls back to alphanumeric ordering based on label if there are order conflicts.
     *
     * @param priority new priority
     * @return this layer
     */
    @NonNull
    public Layer setPriority(int priority) {
        this.priority = priority;
        return this;
    }

    /**
     * Get the z-index for this layer. Used in determining what layers are visually on top of other layers.
     * <p>
     * Falls back to alphanumeric ordering based on name if there are order conflicts.
     * <p>
     * Defaults to '<code>{@link #getPriority()}</code>' if null.
     *
     * @return layer z-index
     */
    @Nullable
    public Integer getZIndex() {
        return zIndex == null ? this.getPriority() : zIndex;
    }

    /**
     * Set the z-index for this layer. Used in determining what layers are visually on top of other layers.
     * <p>
     * Falls back to alphanumeric ordering based on name if there are order conflicts.
     * <p>
     * Defaults to '<code>{@link #getPriority()}</code>' if null.
     *
     * @param zIndex new z-index
     * @return this layer
     */
    @NonNull
    public Layer setZIndex(@Nullable Integer zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    /**
     * Get the map pane for this layer.
     *
     * @return map pane
     */
    @Nullable
    public String getPane() {
        return this.pane;
    }

    /**
     * Set the map pane for this layer.
     * <p>
     * If the pane does not exist, it will be created the first time it is used.
     *
     * @param pane new map pane
     * @return this layer
     */
    @NonNull
    public Layer setPane(@Nullable String pane) {
        this.pane = pane;
        return this;
    }

    /**
     * Get the custom CSS to add for this layer.
     *
     * @return custom CSS
     */
    @Nullable
    public String getCss() {
        return this.css;
    }

    /**
     * Set the custom CSS to add for this layer.
     *
     * @param css new custom CSS
     * @return this layer
     */
    @NonNull
    public Layer setCss(@Nullable String css) {
        this.css = css;
        return this;
    }

    /**
     * Get the markers to display in this Layer.
     *
     * @return markers to display
     */
    @NonNull
    public abstract Collection<Marker<?>> getMarkers();

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
        Layer other = (Layer) o;
        return getKey().equals(other.getKey())
                && getLabel().equals(other.getLabel())
                && getUpdateInterval() == other.getUpdateInterval()
                && shouldShowControls() == other.shouldShowControls()
                && isDefaultHidden() == other.isDefaultHidden()
                && getPriority() == other.getPriority()
                && Objects.equals(getZIndex(), other.getZIndex())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getCss(), other.getCss());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getLabel(), getUpdateInterval(), shouldShowControls(), isDefaultHidden(), getPriority(), getZIndex(), getPane(), getCss());
    }

    @Override
    @NonNull
    public String toString() {
        return "Layer{"
                + "key=" + getKey()
                + ",label=" + getLabel()
                + ",updateInterval=" + getUpdateInterval()
                + ",showControls=" + shouldShowControls()
                + ",defaultHidden=" + isDefaultHidden()
                + ",priority=" + getPriority()
                + ",zIndex=" + getZIndex()
                + ",pane=" + getPane()
                + ",css=" + getCss()
                + "}";
    }
}
