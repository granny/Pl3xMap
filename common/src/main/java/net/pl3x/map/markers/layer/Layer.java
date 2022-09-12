package net.pl3x.map.markers.layer;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.function.Supplier;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.markers.marker.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a layer of markers and other metadata.
 */
public abstract class Layer extends Keyed {
    private Supplier<String> labelSupplier;
    private int updateInterval = 15;
    private boolean showControls = true;
    private boolean defaultHidden = false;
    private int priority = 99;
    private Integer zIndex = 99;

    /**
     * Create a layer.
     *
     * @param key key for layer
     */
    public Layer(@NotNull Key key, @NotNull Supplier<String> labelSupplier) {
        super(key);
        this.labelSupplier = labelSupplier;
    }

    /**
     * Get the label of this layer, shown in the control box.
     *
     * @return layer label
     */
    @NotNull
    public String getLabel() {
        return this.labelSupplier.get();
    }

    /**
     * Set the label of this layer, shown in the control box.
     *
     * @param label new label
     * @return this layer
     */
    @NotNull
    public Layer setLabel(@NotNull String label) {
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
    @NotNull
    public Layer setLabel(@NotNull Supplier<String> labelSupplier) {
        Preconditions.checkNotNull(labelSupplier, "Layer labelSupplier is null");
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
    @NotNull
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
    @NotNull
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
    @NotNull
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
    @NotNull
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
    @NotNull
    public Layer setZIndex(@Nullable Integer zIndex) {
        this.zIndex = zIndex;
        return this;
    }

    /**
     * Get the markers to display in this Layer.
     *
     * @return markers to display
     */
    @NotNull
    public abstract Collection<Marker<?>> getMarkers();
}
