package net.pl3x.map.bukkit;

import java.util.UUID;
import net.pl3x.map.bukkit.command.BukkitCommandManager;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.player.Player;
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
    private final PlayerListener playerListener = new PlayerListener();

    public Pl3xMapBukkit() {
        super();
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onEnable() {
        this.pl3xmap.enable();

        getServer().getPluginManager().registerEvents(this, this);

        getServer().getScheduler().runTaskTimer(this, () -> this.pl3xmap.getScheduler().tick(), 1, 1);

        try {
            new BukkitCommandManager(this);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onDisable() {
        getServer().getScheduler().cancelTasks(this);

        this.pl3xmap.disable();
    }

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onPlayerJoin(@NonNull PlayerJoinEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        UUID uuid = event.getPlayer().getUniqueId();
        Player bukkitPlayer = registry.getOrDefault(uuid, () -> new BukkitPlayer(event.getPlayer()));
        this.playerListener.onJoin(bukkitPlayer);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NonNull PlayerQuitEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        String uuid = event.getPlayer().getUniqueId().toString();
        Player bukkitPlayer = registry.unregister(uuid);
        if (bukkitPlayer != null) {
            this.playerListener.onQuit(bukkitPlayer);
        }
    }
}
