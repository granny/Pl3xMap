package net.pl3x.map.player;

import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.player.MapPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        MapPlayer mapPlayer = Pl3xMap.api().getPlayerManager().getPlayer(player.getUniqueId());
        if (mapPlayer.isHidden()) {
            mapPlayer.setHidden(true, false);
        }
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
