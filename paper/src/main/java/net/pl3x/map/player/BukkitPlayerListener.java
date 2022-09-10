package net.pl3x.map.player;

import net.pl3x.map.Pl3xMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitPlayerListener implements PlayerListener, Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        onJoin(((BukkitPlayerRegistry) Pl3xMap.api().getPlayerRegistry()).register(event.getPlayer()));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        onQuit(((BukkitPlayerRegistry) Pl3xMap.api().getPlayerRegistry()).unregister(event.getPlayer()));
    }
}
