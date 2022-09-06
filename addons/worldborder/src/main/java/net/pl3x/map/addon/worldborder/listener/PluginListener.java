package net.pl3x.map.addon.worldborder.listener;

import java.util.Locale;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.worldborder.border.BorderType;
import net.pl3x.map.addon.worldborder.layer.WorldBorderLayer;
import net.pl3x.map.event.EventHandler;
import net.pl3x.map.markers.layer.Layer;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.PluginEvent;

public class PluginListener implements Listener {
    @EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
        handle(event);
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        handle(event);
    }

    private void handle(PluginEvent event) {
        try {
            BorderType.valueOf(event.getPlugin().getName().toUpperCase(Locale.ROOT));
        } catch (Throwable ignore) {
            return;
        }
        Pl3xMap.api().getWorldRegistry().entries().forEach((key, mapWorld) -> {
            Layer layer = mapWorld.getLayerRegistry().get(WorldBorderLayer.KEY);
            if (layer instanceof WorldBorderLayer worldBorderLayer) {
                worldBorderLayer.clearBorder();
            }
        });
    }
}
