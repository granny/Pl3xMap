package net.pl3x.map.bukkit;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.player.PlayerRegistry;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Pl3xMapBukkit extends JavaPlugin implements Listener {
    private final Pl3xMap pl3xmap;
    private final PlayerListener playerListener = new PlayerListener() {
    };

    public Pl3xMapBukkit() {
        super();
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onEnable() {
        this.pl3xmap.enable();

        getServer().getPluginManager().registerEvents(this, this);

        getServer().getScheduler().runTaskTimer(this, () -> this.pl3xmap.getScheduler().tick(), 1, 1);
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);

        this.pl3xmap.disable();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(@NonNull PlayerJoinEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        this.playerListener.onJoin(registry.register(event.getPlayer().getUniqueId().toString(), new BukkitPlayer(this, event.getPlayer())));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NonNull PlayerQuitEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        this.playerListener.onQuit(registry.unregister(event.getPlayer().getUniqueId().toString()));
    }
}
