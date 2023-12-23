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
package net.pl3x.map.core.renderer.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.JsonObjectWrapper;
import net.pl3x.map.core.markers.layer.Layer;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.scheduler.Task;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.util.TickUtil;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

public class UpdateMarkerData extends Task {
    protected final Gson gson = new GsonBuilder()
            //.setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .registerTypeHierarchyAdapter(Marker.class, new Adapter())
            .create();

    protected final World world;
    protected final Map<@NotNull String, @NotNull Long> lastUpdated = new HashMap<>();
    protected final Map<@NotNull String, @NotNull Long> lastUpdatedSSE = new HashMap<>();
    private final ExecutorService executor;

    private CompletableFuture<Void> future;
    private boolean running;
    private int tempTick;

    public UpdateMarkerData(@NotNull World world) {
        super(1, true);
        this.world = world;
        this.executor = Pl3xMap.ThreadFactory.createService("Pl3xMap-Markers");
    }

    @Override
    public void run() {
        if (this.running) {
            return;
        }
        this.running = true;
        this.future = CompletableFuture.runAsync(() -> {
            try {
                tempTick++;
                parseLayers();
            } catch (Throwable t) {
                t.printStackTrace();
            }
            this.running = false;
        }, this.executor);
    }

    @Override
    public void cancel() {
        super.cancel();
        if (this.future != null) {
            this.future.cancel(true);
        }
    }

    private void parseLayers() {
        List<Object> layers = new ArrayList<>();

        this.world.getLayerRegistry().entrySet().forEach(entry -> {
            String key = entry.getKey();
            Layer layer = entry.getValue();
            try {
                layers.add(layer.toJson());

                long now = System.currentTimeMillis() / 1000;
                long lastUpdate = this.lastUpdated.getOrDefault(key, 0L);
                long lastUpdateSSE = this.lastUpdatedSSE.getOrDefault(key, 0L);

                List<Marker<?>> list = null;
                if (now - lastUpdateSSE >= TickUtil.toSeconds(layer.getSseUpdateInterval())) { // TODO: this is bad
                    list = list == null ? new ArrayList<>(layer.getMarkers()) : list;
                    Pl3xMap.api().getHttpdServer().sendSSE("markers", String.format("{ \"world\": \"%s\", \"key\": \"%s\", \"markers\": %s}", this.world.getName(), key, this.gson.toJson(list)));
                    this.lastUpdatedSSE.put(key, now);
                }
                if (now - lastUpdate > layer.getUpdateInterval()) {
                    list = list == null ? new ArrayList<>(layer.getMarkers()) : list;
                    FileUtil.writeJson(this.gson.toJson(list), this.world.getMarkersDirectory().resolve(key.replace(":", "-") + ".json"));
                    this.lastUpdated.put(key, now);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        });

        if (tempTick >= 20) {
            FileUtil.writeJson(this.gson.toJson(layers), this.world.getTilesDirectory().resolve("markers.json"));
            tempTick = 0;
        }
    }

    private static class Adapter implements JsonSerializer<@NotNull Marker<?>> {
        @Override
        public @NotNull JsonElement serialize(@NotNull Marker<?> marker, @NotNull Type type, @NotNull JsonSerializationContext context) {
            JsonObjectWrapper wrapper = new JsonObjectWrapper();
            wrapper.addProperty("type", marker.getType());
            wrapper.addProperty("data", marker);
            wrapper.addProperty("options", marker.getOptions());
            return wrapper.getJsonObject();
        }
    }
}
