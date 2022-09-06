package net.pl3x.map.addon.worldborder.listener;

import io.papermc.paper.event.world.border.WorldBorderBoundsChangeEvent;
import io.papermc.paper.event.world.border.WorldBorderBoundsChangeFinishEvent;
import io.papermc.paper.event.world.border.WorldBorderCenterChangeEvent;
import io.papermc.paper.event.world.border.WorldBorderEvent;
import net.minecraft.server.level.ServerLevel;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.worldborder.border.Border;
import net.pl3x.map.addon.worldborder.layer.WorldBorderLayer;
import net.pl3x.map.event.EventHandler;
import net.pl3x.map.event.EventListener;
import net.pl3x.map.event.world.WorldLoadedEvent;
import net.pl3x.map.event.world.WorldUnloadedEvent;
import net.pl3x.map.markers.layer.Layer;
import net.pl3x.map.world.MapWorld;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
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

    @org.bukkit.event.EventHandler
    public void on(WorldBorderBoundsChangeEvent event) {
        updateBorder(event);
    }

    @org.bukkit.event.EventHandler
    public void on(WorldBorderBoundsChangeFinishEvent event) {
        updateBorder(event);
    }

    @org.bukkit.event.EventHandler
    public void on(WorldBorderCenterChangeEvent event) {
        updateBorder(event);
    }

    public void updateBorder(WorldBorderEvent event) {
        ServerLevel level = ((CraftWorld) event.getWorld()).getHandle();
        MapWorld mapWorld = Pl3xMap.api().getWorldRegistry().get(level);
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
