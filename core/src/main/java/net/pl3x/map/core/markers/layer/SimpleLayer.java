/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.markers.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.pl3x.map.core.markers.marker.Marker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a simple layer of markers and other metadata.
 */
public class SimpleLayer extends Layer {
    private final Map<@NotNull String, @NotNull Marker<?>> markers = new ConcurrentHashMap<>();

    /**
     * Create a new simple layer.
     *
     * @param key key for layer
     */
    public SimpleLayer(@NotNull String key, @NotNull Supplier<@NotNull String> labelSupplier) {
        super(key, labelSupplier);
    }

    @Override
    public @NotNull Collection<@NotNull Marker<?>> getMarkers() {
        return this.markers.values();
    }

    /**
     * Add a new marker to this layer.
     *
     * @param marker marker
     * @return this layer
     */
    public @NotNull SimpleLayer addMarker(@NotNull Marker<?> marker) {
        this.markers.put(marker.getKey(), marker);
        return this;
    }

    /**
     * Remove a marker from this layer, returning either the removed marker, or {@code null} if
     * no marker was present for the provided key.
     *
     * @param key key
     * @return the existing marker or {@code null}
     */
    public @Nullable Marker<?> removeMarker(@NotNull String key) {
        return this.markers.remove(key);
    }

    /**
     * Remove all registered markers
     */
    public @NotNull SimpleLayer clearMarkers() {
        this.markers.clear();
        return this;
    }

    /**
     * Get an unmodifiable view of the registered markers.
     *
     * @return registered markers
     */
    public @NotNull Map<@NotNull String, @NotNull Marker<?>> registeredMarkers() {
        return Collections.unmodifiableMap(this.markers);
    }

    /**
     * Check whether a marker is registered for a key.
     *
     * @param key key
     * @return true if marker is registered
     */
    public boolean hasMarker(@NotNull String key) {
        return this.markers.containsKey(key);
    }
}
