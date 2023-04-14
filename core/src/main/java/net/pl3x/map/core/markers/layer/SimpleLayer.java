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
    private final Map<String, Marker<?>> markers = new ConcurrentHashMap<>();

    /**
     * Create a new simple layer.
     *
     * @param key key for layer
     */
    public SimpleLayer(@NonNull String key, @NonNull Supplier<String> labelSupplier) {
        super(key, labelSupplier);
    }

    @Override
    @NonNull
    public Collection<Marker<?>> getMarkers() {
        return this.markers.values();
    }

    /**
     * Add a new marker to this layer.
     *
     * @param key    key
     * @param marker marker
     * @return this layer
     */
    @NonNull
    public SimpleLayer addMarker(@NonNull String key, @NonNull Marker<?> marker) {
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
    @Nullable
    public Marker<?> removeMarker(@NonNull String key) {
        return this.markers.remove(key);
    }

    /**
     * Remove all registered markers
     */
    @NonNull
    public SimpleLayer clearMarkers() {
        this.markers.clear();
        return this;
    }

    /**
     * Get an unmodifiable view of the registered markers.
     *
     * @return registered markers
     */
    @NonNull
    public Map<String, Marker<?>> registeredMarkers() {
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
