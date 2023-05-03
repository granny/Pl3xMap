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

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.FileReader;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents a custom layer of markers and other metadata.
 */
public class CustomLayer extends WorldLayer {
    private final Map<@NonNull String, @NonNull Marker<@NonNull ?>> markers = new ConcurrentHashMap<>();

    /**
     * Create a new custom layer.
     *
     * @param key key for layer
     */
    public CustomLayer(@NonNull String key, @NonNull World world, @NonNull Supplier<@NonNull String> labelSupplier) {
        super(key, world, labelSupplier);
    }

    @Override
    public @NonNull Collection<@NonNull Marker<@NonNull ?>> getMarkers() {
        return this.markers.values();
    }

    public static void load(World world, Path path) {
        CustomLayer layer;
        try {
            layer = fromJson(world, (JsonObject) JsonParser.parseReader(new FileReader(path.toFile())));
        } catch (Throwable t) {
            Logger.severe("Error reading custom marker file: " + path.toAbsolutePath(), t);
            return;
        }
        System.out.println("Layer: " + layer);
    }

    public static @NonNull CustomLayer fromJson(@NonNull World world, @NonNull JsonObject obj) {
        JsonElement el;
        CustomLayer layer = new CustomLayer(obj.get("key").getAsString(), world, () -> obj.get("label").getAsString());
        if ((el = obj.get("updateInterval")) != null && !(el instanceof JsonNull)) layer.setUpdateInterval(el.getAsInt());
        if ((el = obj.get("showControls")) != null && !(el instanceof JsonNull)) layer.setShowControls(el.getAsBoolean());
        if ((el = obj.get("defaultHidden")) != null && !(el instanceof JsonNull)) layer.setDefaultHidden(el.getAsBoolean());
        if ((el = obj.get("priority")) != null && !(el instanceof JsonNull)) layer.setPriority(el.getAsInt());
        if ((el = obj.get("zIndex")) != null && !(el instanceof JsonNull)) layer.setZIndex(el.getAsInt());
        if ((el = obj.get("pane")) != null && !(el instanceof JsonNull)) layer.setPane(el.getAsString());
        if ((el = obj.get("css")) != null && !(el instanceof JsonNull)) layer.setCss(el.getAsString());
        return layer;
    }
}
