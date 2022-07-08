package net.pl3x.map.task;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.WorldManager;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateWorldData extends BukkitRunnable {
    private final Gson gson = new Gson().newBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void run() {
        List<Object> worlds = new ArrayList<>();

        WorldManager.INSTANCE.getMapWorlds().forEach(mapWorld -> {
            WorldConfig config = mapWorld.getConfig();

            if (!config.ENABLED) {
                return;
            }

            Map<String, Object> spawn = new HashMap<>();
            Location loc = mapWorld.getWorld().getSpawnLocation();
            spawn.put("x", loc.getBlockX());
            spawn.put("z", loc.getBlockZ());

            Map<String, Object> zoom = new HashMap<>();
            zoom.put("default", config.ZOOM_DEFAULT);
            zoom.put("max_out", config.ZOOM_MAX_OUT);
            zoom.put("max_in", config.ZOOM_MAX_IN);

            Map<String, Object> settings = new HashMap<>();
            settings.put("spawn", spawn);
            settings.put("zoom", zoom);
            settings.put("tiles_update_interval", config.RENDER_BACKGROUND_INTERVAL);

            FileUtil.write(gson.toJson(settings), mapWorld.getWorldTilesDir().resolve("settings.json"));

            Map<String, Object> worldsList = new HashMap<>();
            worldsList.put("name", mapWorld.getName());
            worldsList.put("display_name", config.DISPLAY_NAME
                    .replace("<world>", mapWorld.getName()));
            worldsList.put("icon", config.ICON);
            worldsList.put("type", mapWorld.getWorld().getEnvironment().name().toLowerCase());
            worldsList.put("order", config.ORDER);
            worlds.add(worldsList);
        });

        Map<String, Object> ui = new HashMap<>();
        ui.put("coords", Config.UI_COORDS_ENABLED);
        ui.put("link", Config.UI_LINK_ENABLED);

        Map<String, Object> lang = new HashMap<>();
        lang.put("title", Lang.UI_TITLE);
        lang.put("players", Lang.UI_PLAYERS);
        lang.put("worlds", Lang.UI_WORLDS);
        lang.put("coords", Lang.UI_COORDS);
        ui.put("lang", lang);

        Map<String, Object> map = new HashMap<>();
        map.put("worlds", worlds);
        map.put("ui", ui);

        FileUtil.write(gson.toJson(map), MapWorld.TILES_DIR.resolve("settings.json"));
    }
}
