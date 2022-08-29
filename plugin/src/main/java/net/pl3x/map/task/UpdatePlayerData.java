package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.player.MapPlayer;
import net.pl3x.map.api.player.PlayerManager;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
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

        PlayerManager playerManager = Pl3xMap.api().getPlayerManager();

        Bukkit.getWorlds().forEach(world -> {
            MapWorld mapWorld = Pl3xMap.api().getWorldManager().getMapWorld(world);

            world.getPlayers().forEach(player -> {
                if (mapWorld != null && mapWorld.getConfig().PLAYER_TRACKER_HIDE_SPECTATORS && player.getGameMode() == GameMode.SPECTATOR) {
                    return;
                }
                if (mapWorld != null && mapWorld.getConfig().PLAYER_TRACKER_HIDE_INVISIBLE && player.isInvisible()) {
                    return;
                }
                if (player.hasMetadata("NPC")) {
                    return;
                }
                MapPlayer mapPlayer = playerManager.getPlayer(player.getUniqueId());
                if (mapPlayer.isHidden()) {
                    return;
                }
                Map<String, Object> playerEntry = new LinkedHashMap<>();
                Location loc = player.getLocation();

                playerEntry.put("name", playerManager.decorateName(mapPlayer));
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

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("max", Bukkit.getMaxPlayers());
        map.put("players", players);

        FileUtil.write(this.gson.toJson(map), MapWorld.TILES_DIR.resolve("players.json"));
    }

    private static int getArmorPoints(Player player) {
        AttributeInstance attr = player.getAttribute(Attribute.GENERIC_ARMOR);
        return attr == null ? 0 : (int) attr.getValue();
    }
}
