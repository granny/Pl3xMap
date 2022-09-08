package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.markers.Point;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;

public class UpdateWorldData implements Runnable {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void run() {
        List<Map<String, Object>> worlds = new ArrayList<>();

        Pl3xMap.api().getWorldRegistry().entries().forEach((key, world) -> {
            WorldConfig config = world.getConfig();

            if (!config.ENABLED) {
                return;
            }

            Map<String, Object> spawn = new LinkedHashMap<>();
            Point point = world.getSpawn();
            spawn.put("x", point.getX());
            spawn.put("z", point.getZ());

            Map<String, Object> zoom = new LinkedHashMap<>();
            zoom.put("default", config.ZOOM_DEFAULT);
            zoom.put("max_out", config.ZOOM_MAX_OUT);
            zoom.put("max_in", config.ZOOM_MAX_IN);

            Map<String, Object> playerTracker = new LinkedHashMap<>();
            playerTracker.put("enabled", config.PLAYER_TRACKER_ENABLED);
            playerTracker.put("interval", config.PLAYER_TRACKER_INTERVAL);
            playerTracker.put("label", Lang.UI_PLAYER_TRACKER);
            playerTracker.put("show_controls", config.PLAYER_TRACKER_SHOW_CONTROLS);
            playerTracker.put("default_hidden", config.PLAYER_TRACKER_DEFAULT_HIDDEN);
            playerTracker.put("priority", config.PLAYER_TRACKER_PRIORITY);
            playerTracker.put("z_index", config.PLAYER_TRACKER_Z_INDEX);

            Map<String, Object> nameplates = new LinkedHashMap<>();
            nameplates.put("enabled", config.PLAYER_TRACKER_NAMEPLATE_ENABLED);
            nameplates.put("show_heads", config.PLAYER_TRACKER_NAMEPLATE_SHOW_HEAD);
            nameplates.put("show_armor", config.PLAYER_TRACKER_NAMEPLATE_SHOW_ARMOR);
            nameplates.put("show_health", config.PLAYER_TRACKER_NAMEPLATE_SHOW_HEALTH);
            playerTracker.put("nameplates", nameplates);

            Map<String, Object> ui = new LinkedHashMap<>();
            ui.put("link", config.UI_LINK);
            ui.put("coords", config.UI_COORDS);
            ui.put("blockinfo", config.UI_BLOCKINFO);
            ui.put("hide_attributes", config.UI_HIDE_ATTRIBUTES);

            Map<String, Object> settings = new LinkedHashMap<>();
            settings.put("name", world.getName());
            settings.put("renderers", config.RENDER_RENDERERS);
            settings.put("tiles_update_interval", config.RENDER_BACKGROUND_INTERVAL);
            settings.put("spawn", spawn);
            settings.put("zoom", zoom);
            settings.put("player_tracker", playerTracker);
            settings.put("ui", ui);

            FileUtil.write(this.gson.toJson(settings), world.getTilesDir().resolve("settings.json"));

            Map<String, Object> worldsList = new LinkedHashMap<>();
            worldsList.put("name", world.getName());
            worldsList.put("display_name", config.DISPLAY_NAME
                    .replace("<world>", world.getName()));
            worldsList.put("type", world.getType().toString());
            worldsList.put("order", config.ORDER);
            worlds.add(worldsList);
        });

        // sort worlds by order, then by name
        worlds.sort(Comparator.<Map<String, Object>>comparingInt(w -> (int) w.get("order")).thenComparing(w -> (String) w.get("name")));

        Map<String, Object> lang = new LinkedHashMap<>();
        lang.put("title", Lang.UI_TITLE);
        lang.put("players", Lang.UI_PLAYERS);
        lang.put("worlds", Map.of("heading", Lang.UI_WORLDS_HEADING, "skeleton", Lang.UI_WORLDS_SKELETON));
        lang.put("layers", Map.of("heading", Lang.UI_LAYERS_HEADING, "skeleton", Lang.UI_LAYERS_SKELETON));
        lang.put("coords", Map.of("label", Lang.UI_COORDS_LABEL, "value", Lang.UI_COORDS_VALUE));

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("worlds", worlds);
        map.put("format", Config.WEB_TILE_FORMAT);
        map.put("lang", lang);

        FileUtil.write(this.gson.toJson(map), World.TILES_DIR.resolve("settings.json"));
    }
}
