package net.pl3x.map.addon.worldborder.listener;

import net.pl3x.map.api.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;

public class PluginListener implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        String name = event.getPlugin().getName();
    }
}
