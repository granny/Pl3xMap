package net.pl3x.map.player;

import net.pl3x.map.world.WorldManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        PlayerManager manager = PlayerManager.INSTANCE;
        if (manager.isHidden(player)) {
            manager.setHidden(player, manager.isHidden(player), false);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        PlayerManager.INSTANCE.setHidden(player, false, false);
        WorldManager.INSTANCE.getMapWorlds().forEach(mapWorld -> {
            if (!mapWorld.hasActiveRender()) {
                return;
            }
            mapWorld.getActiveRender().getProgress().hide(player);
        });
    }
}
