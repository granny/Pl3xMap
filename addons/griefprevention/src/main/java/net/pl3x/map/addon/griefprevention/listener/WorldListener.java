package net.pl3x.map.addon.griefprevention.listener;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.griefprevention.GriefPrevention;
import net.pl3x.map.addon.griefprevention.layer.GPLayer;
import net.pl3x.map.event.EventHandler;
import net.pl3x.map.event.EventListener;
import net.pl3x.map.event.server.ServerLoadedEvent;
import net.pl3x.map.event.world.WorldLoadedEvent;
import net.pl3x.map.event.world.WorldUnloadedEvent;

public class WorldListener implements EventListener {
    private final GriefPrevention addon;

    public WorldListener(GriefPrevention addon) {
        this.addon = addon;
    }

    @EventHandler
    public void onServerLoaded(ServerLoadedEvent event) {
        Pl3xMap.api().getWorldRegistry().entries().forEach((key, world) ->
                world.getLayerRegistry().register(new GPLayer(this.addon, world))
        );
    }

    @EventHandler
    public void onWorldLoaded(WorldLoadedEvent event) {
        event.getWorld().getLayerRegistry().register(new GPLayer(this.addon, event.getWorld()));
    }

    @EventHandler
    public void onWorldUnloaded(WorldUnloadedEvent event) {
        event.getWorld().getLayerRegistry().unregister(GPLayer.KEY);
    }
}
