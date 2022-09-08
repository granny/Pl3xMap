package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.markers.Point;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;

public class UpdatePlayerData implements Runnable {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void run() {
        List<Object> players = new ArrayList<>();

        Pl3xMap.api().getPlayerRegistry().entries().forEach((key, player) -> {
            if (player.isNPC()) {
                return;
            }
            if (player.isHidden()) {
                return;
            }
            Map<String, Object> entry = new LinkedHashMap<>();
            Point position = player.getPosition();

            entry.put("name", player.getDecoratedName());
            entry.put("uuid", player.getUUID().toString());
            entry.put("world", player.getWorld().getName());

            if (player.getWorld().getConfig().PLAYER_TRACKER_ENABLED) {
                entry.put("x", position.getX());
                entry.put("z", position.getZ());
                entry.put("yaw", player.getYaw());
                if (player.getWorld().getConfig().PLAYER_TRACKER_NAMEPLATE_SHOW_ARMOR) {
                    entry.put("armor", player.getArmorPoints());
                }
                if (player.getWorld().getConfig().PLAYER_TRACKER_NAMEPLATE_SHOW_HEALTH) {
                    entry.put("health", player.getHealth());
                }
            }

            players.add(entry);
        });

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("max", MinecraftServer.getServer().getMaxPlayers());
        map.put("players", players);

        FileUtil.write(this.gson.toJson(map), World.TILES_DIR.resolve("players.json"));
    }
}
