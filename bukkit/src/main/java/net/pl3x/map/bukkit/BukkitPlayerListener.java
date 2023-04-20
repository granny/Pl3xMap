package net.pl3x.map.bukkit;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.player.PlayerRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitPlayerListener implements PlayerListener, Listener {
    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(@NonNull PlayerJoinEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        onJoin(registry.register(event.getPlayer().getUniqueId().toString(), new BukkitPlayer(event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NonNull PlayerQuitEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        onQuit(registry.unregister(event.getPlayer().getUniqueId().toString()));
    }
}
