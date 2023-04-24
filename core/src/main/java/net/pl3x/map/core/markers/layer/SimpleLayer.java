package net.pl3x.map.core.markers.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.pl3x.map.core.markers.marker.Marker;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;


/**
 * Represents a simple layer of markers and other metadata.
 */
public class SimpleLayer extends Layer {
    private final Map<@NonNull String, @NonNull Marker<@NonNull ?>> markers = new ConcurrentHashMap<>();

    /**
     * Create a new simple layer.
     *
     * @param key key for layer
     */
    public SimpleLayer(@NonNull String key, @NonNull Supplier<@NonNull String> labelSupplier) {
        super(key, labelSupplier);
    }

    @Override
    public @NonNull Collection<@NonNull Marker<@NonNull ?>> getMarkers() {
        return this.markers.values();
    }

    /**
     * Add a new marker to this layer.
     *
     * @param key    key
     * @param marker marker
     * @return this layer
     */
    public @NonNull SimpleLayer addMarker(@NonNull String key, @NonNull Marker<@NonNull ?> marker) {
        this.markers.put(key, marker);
        return this;
    }

    /**
     * Remove a marker from this layer, returning either the removed marker, or {@code null} if
     * no marker was present for the provided key.
     *
     * @param key key
     * @return the existing marker or {@code null}
     */
    public @Nullable Marker<@NonNull ?> removeMarker(@NonNull String key) {
        return this.markers.remove(key);
    }

    /**
     * Remove all registered markers
     */
    public @NonNull SimpleLayer clearMarkers() {
        this.markers.clear();
        return this;
    }

    /**
     * Get an unmodifiable view of the registered markers.
     *
     * @return registered markers
     */
    public @NonNull Map<@NonNull String, @NonNull Marker<@NonNull ?>> registeredMarkers() {
        return Collections.unmodifiableMap(this.markers);
    }

    /**
     * Check whether a marker is registered for a key.
     *
     * @param key key
     * @return true if marker is registered
     */
    public boolean hasMarker(@NonNull String key) {
        return this.markers.containsKey(key);
    }
}
