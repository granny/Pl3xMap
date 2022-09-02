package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateMarkerData extends BukkitRunnable {
    private final MapWorld mapWorld;
    private final Map<Key, Long> lastUpdated = new HashMap<>();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    public UpdateMarkerData(MapWorld mapWorld) {
        this.mapWorld = mapWorld;
    }

    @Override
    public void run() {
        Map<String, Integer> layers = new HashMap<>();

        this.mapWorld.getLayerRegistry().entries().forEach((key, layer) -> {
            layers.put(key.getKey(), layer.getUpdateInterval());

            long now = System.currentTimeMillis() / 1000;
            long lastUpdate = this.lastUpdated.getOrDefault(key, 0L);

            if (now - lastUpdate > layer.getUpdateInterval()) {
                FileUtil.write(Marker.GSON.toJson(layer.getMarkers()), this.mapWorld.getMarkersDir().resolve(key + ".json"));
                this.lastUpdated.put(key, now);
            }
        });

        FileUtil.write(this.gson.toJson(layers), this.mapWorld.getTilesDir().resolve("markers.json"));
    }
}
