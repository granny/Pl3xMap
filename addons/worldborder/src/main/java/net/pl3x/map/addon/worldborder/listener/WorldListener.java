package net.pl3x.map.addon.worldborder.listener;

import net.pl3x.map.addon.worldborder.layer.WorldBorderLayer;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.event.EventHandler;
import net.pl3x.map.api.event.EventListener;
import net.pl3x.map.api.event.world.WorldLoadedEvent;
import net.pl3x.map.api.event.world.WorldUnloadedEvent;
import net.pl3x.map.api.marker.layer.Layer;
import net.pl3x.map.api.registry.LayerRegistry;

public class WorldListener implements EventListener {
    private static final String PREFIX = "world-border-";

    @EventHandler
    public void onWorldLoaded(WorldLoadedEvent event) {
        Key key = new Key(PREFIX + event.getWorld().getName());
        Layer layer = new WorldBorderLayer(event.getWorld());

        LayerRegistry registry = Pl3xMap.api().getLayerRegistry();
        registry.register(key, layer);
    }

    @EventHandler
    public void onWorldUnloaded(WorldUnloadedEvent event) {
        Key key = new Key(PREFIX + event.getWorld().getName());

        LayerRegistry registry = Pl3xMap.api().getLayerRegistry();
        registry.unregister(key);
    }
}
