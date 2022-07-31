package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.pl3x.map.player.PlayerManager;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdatePlayerData extends BukkitRunnable {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void run() {
        List<Object> players = new ArrayList<>();

        Bukkit.getWorlds().forEach(world -> {
            MapWorld mapWorld = WorldManager.INSTANCE.getMapWorld(world);

            world.getPlayers().forEach(player -> {
                if (mapWorld != null && mapWorld.getConfig().PLAYER_TRACKER_HIDE_SPECTATORS && player.getGameMode() == GameMode.SPECTATOR) {
                    return;
                }
                if (mapWorld != null && mapWorld.getConfig().PLAYER_TRACKER_HIDE_INVISIBLE && player.isInvisible()) {
                    return;
                }
                if (PlayerManager.INSTANCE.isHidden(player)) {
                    return;
                }
                if (player.hasMetadata("NPC")) {
                    return;
                }
                Map<String, Object> playerEntry = new HashMap<>();
                Location loc = player.getLocation();

                playerEntry.put("name", PlayerManager.INSTANCE.decorateName(player));
                playerEntry.put("uuid", player.getUniqueId().toString().replace("-", ""));
                playerEntry.put("world", loc.getWorld().getName());

                if (mapWorld != null && mapWorld.getConfig().PLAYER_TRACKER_ENABLED) {
                    playerEntry.put("x", loc.getBlockX());
                    playerEntry.put("z", loc.getBlockZ());
                    playerEntry.put("yaw", loc.getYaw());
                    if (mapWorld.getConfig().PLAYER_TRACKER_NAMEPLATE_SHOW_ARMOR) {
                        playerEntry.put("armor", getArmorPoints(player));
                    }
                    if (mapWorld.getConfig().PLAYER_TRACKER_NAMEPLATE_SHOW_HEALTH) {
                        playerEntry.put("health", (int) player.getHealth());
                    }
                }

                players.add(playerEntry);
            });
        });

        Map<String, Object> map = new HashMap<>();
        map.put("players", players);
        map.put("max", Bukkit.getMaxPlayers());

        FileUtil.write(this.gson.toJson(map), MapWorld.TILES_DIR.resolve("players.json"));
    }

    private static int getArmorPoints(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_ARMOR);
        return attr == null ? 0 : (int) attr.getValue();
    }
}
