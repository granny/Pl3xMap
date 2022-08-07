package net.pl3x.map.player;

import java.net.URL;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.player.MapPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final Pl3xMapPlugin plugin;

    public PlayerListener(Pl3xMapPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        MapPlayer mapPlayer = ((BukkitPlayerManager) Pl3xMap.api().getPlayerManager()).getPlayer(player);
        if (mapPlayer.isHidden()) {
            mapPlayer.setHidden(true, false);
        }

        URL url = player.getPlayerProfile().getTextures().getSkin();
        new PlayerTexture(player.getUniqueId(), url).runTaskAsynchronously(plugin);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Pl3xMap.api().getPlayerManager().unloadPlayer(player.getUniqueId());
        Pl3xMap.api().getWorldManager().getMapWorlds().forEach(mapWorld -> {
            if (!mapWorld.hasActiveRender()) {
                return;
            }
            mapWorld.getActiveRender().getProgress().hideChat(player);
        });
    }
}
