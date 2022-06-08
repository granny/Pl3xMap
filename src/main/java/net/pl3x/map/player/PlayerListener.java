package net.pl3x.map.player;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.player.PlayerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private final Pl3xMap plugin;

    public PlayerListener(Pl3xMap plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerManager playerManager = plugin.getPlayerManager();
        if (playerManager.isHidden(player)) {
            playerManager.setHidden(player, playerManager.isHidden(player), false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        plugin.getPlayerManager().setHidden(player, false, false);
    }
}
