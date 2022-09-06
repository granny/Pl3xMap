package net.pl3x.map.player;

import java.net.URL;
import net.pl3x.map.Pl3xMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        MapPlayer mapPlayer = ((BukkitPlayerRegistry) Pl3xMap.api().getPlayerRegistry()).getPlayer(player);
        if (mapPlayer.isHidden()) {
            mapPlayer.setHidden(true, false);
        }

        URL url = player.getPlayerProfile().getTextures().getSkin();
        new Thread(() -> new PlayerTexture(player.getUniqueId(), url)).start();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        Pl3xMap.api().getPlayerRegistry().unloadPlayer(player.getUniqueId());
        Pl3xMap.api().getWorldRegistry().entries().forEach((key, mapWorld) -> {
            if (!mapWorld.hasActiveRender()) {
                return;
            }
            mapWorld.getActiveRender().getProgress().hideChat(player);
        });
    }
}
