package net.pl3x.map.addon.worldborder.listener;

import io.papermc.paper.event.world.border.WorldBorderBoundsChangeEvent;
import io.papermc.paper.event.world.border.WorldBorderBoundsChangeFinishEvent;
import io.papermc.paper.event.world.border.WorldBorderCenterChangeEvent;
import io.papermc.paper.event.world.border.WorldBorderEvent;
import net.pl3x.map.addon.worldborder.border.Border;
import net.pl3x.map.addon.worldborder.layer.WorldBorderLayer;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.event.EventHandler;
import net.pl3x.map.api.event.EventListener;
import net.pl3x.map.api.event.world.WorldLoadedEvent;
import net.pl3x.map.api.event.world.WorldUnloadedEvent;
import net.pl3x.map.api.markers.layer.Layer;
import net.pl3x.map.world.MapWorld;
import org.bukkit.event.Listener;

public class WorldListener implements EventListener, Listener {
    @EventHandler
    public void onWorldLoaded(WorldLoadedEvent event) {
        MapWorld mapWorld = event.getWorld();
        mapWorld.getLayerRegistry().register(WorldBorderLayer.KEY, new WorldBorderLayer(mapWorld));
    }

    @EventHandler
    public void onWorldUnloaded(WorldUnloadedEvent event) {
        event.getWorld().getLayerRegistry().unregister(WorldBorderLayer.KEY);
    }

    @EventHandler
    public void on(WorldBorderBoundsChangeEvent event) {
        updateBorder(event);
    }

    @EventHandler
    public void on(WorldBorderBoundsChangeFinishEvent event) {
        updateBorder(event);
    }

    @EventHandler
    public void on(WorldBorderCenterChangeEvent event) {
        updateBorder(event);
    }

    public void updateBorder(WorldBorderEvent event) {
        MapWorld mapWorld = Pl3xMap.api().getWorldManager().getMapWorld(event.getWorld());
        if (mapWorld == null) {
            return;
        }

        Layer layer = mapWorld.getLayerRegistry().get(WorldBorderLayer.KEY);
        if (!(layer instanceof WorldBorderLayer borderLayer)) {
            return;
        }

        Border border = borderLayer.getBorder();
        border.update();
    }
}
