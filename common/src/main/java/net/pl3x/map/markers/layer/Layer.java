package net.pl3x.map.markers.layer;

import java.util.Collection;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.markers.marker.Marker;
import org.jetbrains.annotations.NotNull;

/**
 * Provides Markers and other metadata which make up a Layer
 */
public abstract class Layer extends Keyed {
    /**
     * Create a layer.
     *
     * @param key key for object
     */
    public Layer(@NotNull Key key) {
        super(key);
    }

    /**
     * Get the label of this Layer, shown in the control box
     *
     * @return layer label
     */
    @NotNull
    public abstract String getLabel();

    /**
     * Get this layer's update interval (in seconds).
     *
     * @return update interval
     */
    public abstract int getUpdateInterval();

    /**
     * Whether to show this Layer in the control box
     * <p>
     * Default implementation always returns {@code true}
     *
     * @return true if showing controls
     */
    public boolean showControls() {
        return true;
    }

    /**
     * Whether this Layer is hidden by default in the control box
     * <p>
     * Default implementation always returns {@code false}
     *
     * @return true if hidden by default
     */
    public boolean defaultHidden() {
        return false;
    }

    /**
     * Indexed order for this Layer in the control box
     * <p>
     * Falls back to alphanumeric ordering based on label if there are order conflicts
     *
     * @return layer priority
     */
    public abstract int getPriority();

    /**
     * Indexed z-index for this Layer. Used in determining what layers are visually on top of other layers
     * <p>
     * Falls back to alphanumeric ordering based on name if there are order conflicts
     * <p>
     * Default implementation returns {@link #getPriority()}
     *
     * @return layer z-index
     */
    public int getZIndex() {
        return this.getPriority();
    }

    /**
     * Get the markers to display in this Layer
     *
     * @return markers to display
     */
    @NotNull
    public abstract Collection<Marker> getMarkers();
}
