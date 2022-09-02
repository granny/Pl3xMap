package net.pl3x.map.addon.worldborder.listener;

import net.pl3x.map.addon.worldborder.layer.WorldBorderLayer;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.event.EventHandler;
import net.pl3x.map.api.event.EventListener;
import net.pl3x.map.api.event.world.WorldLoadedEvent;
import net.pl3x.map.api.markers.layer.Layer;
import net.pl3x.map.api.registry.LayerRegistry;
import net.pl3x.map.world.MapWorld;

public class WorldListener implements EventListener {
    private static final String WORLD_BORDER = "world-border";

    @EventHandler
    public void onWorldLoaded(WorldLoadedEvent event) {
        MapWorld mapWorld = event.getWorld();

        Key key = new Key(WORLD_BORDER);
        Layer layer = new WorldBorderLayer(mapWorld);

        LayerRegistry registry = mapWorld.getLayerRegistry();
        registry.register(key, layer);
    }
}
