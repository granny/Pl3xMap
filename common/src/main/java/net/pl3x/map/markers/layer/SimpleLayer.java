package net.pl3x.map.markers.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.pl3x.map.Key;
import net.pl3x.map.markers.marker.Marker;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a simple layer of markers and other metadata.
 */
public class SimpleLayer extends Layer {
    private final Map<Key, Marker<?>> markers = new ConcurrentHashMap<>();

    /**
     * Create a new simple layer.
     *
     * @param key key for layer
     */
    public SimpleLayer(@NotNull Key key, @NotNull Supplier<String> labelSupplier) {
        super(key, labelSupplier);
    }

    @Override
    @NotNull
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
    @NotNull
    public SimpleLayer addMarker(@NonNull Key key, @NotNull Marker<?> marker) {
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
    public Marker<?> removeMarker(@NonNull Key key) {
        return this.markers.remove(key);
    }

    /**
     * Remove all registered markers
     */
    @NotNull
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
    public Map<Key, Marker<?>> registeredMarkers() {
        return Collections.unmodifiableMap(this.markers);
    }

    /**
     * Check whether a marker is registered for a key.
     *
     * @param key key
     * @return true if marker is registered
     */
    public boolean hasMarker(@NonNull Key key) {
        return this.markers.containsKey(key);
    }
}
