package net.pl3x.map.addon.markertest;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import javax.imageio.ImageIO;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.addon.Addon;
import net.pl3x.map.api.event.EventHandler;
import net.pl3x.map.api.event.EventListener;
import net.pl3x.map.api.event.world.WorldLoadedEvent;
import net.pl3x.map.api.event.world.WorldUnloadedEvent;
import net.pl3x.map.api.markers.Line;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.Vector;
import net.pl3x.map.api.markers.layer.Layer;
import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;

public class MarkerTestAddon extends Addon implements EventListener {
    private static final Key LAYER_KEY = new Key("marker-test");
    private static final Key ICON_KEY = new Key("test-x-icon");
    private static final Collection<Marker> MARKERS = new HashSet<>();

    @Override
    public void onEnable() {
        Pl3xMap.api().getEventRegistry().register(this, this);

        try {
            //noinspection ConstantConditions
            BufferedImage image = ImageIO.read(getClass().getResourceAsStream("/icons/x.png"));
            Pl3xMap.api().getIconRegistry().register(ICON_KEY, image);
        } catch (IOException e) {
            Logger.warn("Failed to register spawn icon");
            e.printStackTrace();
        }
    }

    @EventHandler
    public void on(WorldLoadedEvent event) {
        MapWorld mapWorld = event.getWorld();

        MARKERS.add(Marker.circle(Point.of(-100, 100), 20));
        MARKERS.add(Marker.ellipse(Point.of(100, 100), Vector.of(10, 20), 45D));
        MARKERS.add(Marker.icon(Point.of(150, 50), ICON_KEY, 16));
        MARKERS.add(Marker.polyline(Line.of(
                Point.of(0, 0),
                Point.of(50, 50),
                Point.of(100, 0)
        ).loop()));
        //MARKERS.add(Marker.polygon());
        MARKERS.add(Marker.rectangle(Point.of(-50, -50), Point.of(-20, 0)));

        mapWorld.getLayerRegistry().register(LAYER_KEY, new Layer() {
            @Override
            public @NotNull Key getKey() {
                return LAYER_KEY;
            }

            @Override
            public @NotNull String getLabel() {
                return "Marker Test";
            }

            @Override
            public int getUpdateInterval() {
                return 60;
            }

            @Override
            public int getPriority() {
                return 999;
            }

            @Override
            public @NotNull Collection<Marker> getMarkers() {
                return MARKERS;
            }
        });
    }

    @EventHandler
    public void on(WorldUnloadedEvent event) {
        MapWorld mapWorld = event.getWorld();
        mapWorld.getLayerRegistry().unregister(LAYER_KEY);
        Pl3xMap.api().getIconRegistry().unregister(ICON_KEY);
    }
}
