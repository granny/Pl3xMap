package net.pl3x.map.player;

import net.pl3x.map.Pl3xMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BukkitPlayerListener implements PlayerListener, Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent event) {
        BukkitPlayerRegistry registry = (BukkitPlayerRegistry) Pl3xMap.api().getPlayerRegistry();
        onJoin(registry.register(event.getPlayer()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        BukkitPlayerRegistry registry = (BukkitPlayerRegistry) Pl3xMap.api().getPlayerRegistry();
        onQuit(registry.unregister(event.getPlayer()));
    }
}
