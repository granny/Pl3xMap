package net.pl3x.map.player;

import java.net.URL;
import net.pl3x.map.Pl3xMap;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        org.bukkit.entity.Player bukkitPlayer = event.getPlayer();

        Player player = ((BukkitPlayerRegistry) Pl3xMap.api().getPlayerRegistry()).register(bukkitPlayer);
        if (player.isHidden()) {
            player.setHidden(true, false);
        }

        URL url = bukkitPlayer.getPlayerProfile().getTextures().getSkin();
        new Thread(() -> new PlayerTexture(bukkitPlayer.getUniqueId(), url)).start();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        org.bukkit.entity.Player bukkitPlayer = event.getPlayer();
        Player player = ((BukkitPlayerRegistry) Pl3xMap.api().getPlayerRegistry()).unregister(bukkitPlayer);
        if (player == null) {
            return;
        }
        Pl3xMap.api().getWorldRegistry().entries().forEach((key, world) -> {
            if (!world.hasActiveRender()) {
                return;
            }
            world.getActiveRender().getProgress().hideChat(player);
        });
    }
}
