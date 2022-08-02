package net.pl3x.map.render.marker.layer;

import java.util.Collection;
import net.pl3x.map.render.marker.Marker;

/**
 * Provides Markers and other metadata which make up a Layer
 */
public interface Layer {
    /**
     * Get the label of this Layer, shown in the control box
     *
     * @return layer label
     */
    String getLabel();

    /**
     * Whether to show this Layer in the control box
     * <p>
     * Default implementation always returns {@code true}
     *
     * @return true if showing controls
     */
    default boolean showControls() {
        return true;
    }

    /**
     * Whether this Layer is hidden by default in the control box
     * <p>
     * Default implementation always returns {@code false}
     *
     * @return true if hidden by default
     */
    default boolean defaultHidden() {
        return false;
    }

    /**
     * Indexed order for this Layer in the control box
     * <p>
     * Falls back to alphanumeric ordering based on label if there are order conflicts
     *
     * @return layer priority
     */
    int getPriority();

    /**
     * Indexed z-index for this Layer. Used in determining what layers are visually on top of other layers
     * <p>
     * Falls back to alphanumeric ordering based on name if there are order conflicts
     * <p>
     * Default implementation returns {@link #getPriority()}
     *
     * @return layer z-index
     */
    default int getZIndex() {
        return this.getPriority();
    }

    /**
     * Get the markers to display in this Layer
     *
     * @return markers to display
     */
    Collection<Marker> getMarkers();
}
