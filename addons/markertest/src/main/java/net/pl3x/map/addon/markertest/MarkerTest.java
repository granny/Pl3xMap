package net.pl3x.map.addon.markertest;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import javax.imageio.ImageIO;
import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.event.EventHandler;
import net.pl3x.map.event.EventListener;
import net.pl3x.map.event.world.WorldLoadedEvent;
import net.pl3x.map.event.world.WorldUnloadedEvent;
import net.pl3x.map.image.IconImage;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.Vector;
import net.pl3x.map.markers.layer.SimpleLayer;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.markers.marker.Polygon;
import net.pl3x.map.markers.marker.Polyline;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public class MarkerTest extends Addon implements EventListener {
    private static final Key LAYER_KEY = Key.of("marker-test");
    private static final Key ICON_KEY = Key.of("test-x-icon");
    private static final Collection<Marker<?>> MARKERS = new HashSet<>();

    @Override
    public void onEnable() {
        // copy icon to Pl3xMap's icon directory
        FileUtil.extract(getClass(), "x.png", "web/images/icon/", !Config.WEB_DIR_READONLY);

        // register icon
        Path icon = World.WEB_DIR.resolve("images/icon/x.png");
        try {
            IconImage image = new IconImage(ICON_KEY, ImageIO.read(icon.toFile()), "png");
            Pl3xMap.api().getIconRegistry().register(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // register event listener
        Pl3xMap.api().getEventRegistry().register(this);
    }

    @EventHandler
    public void on(WorldLoadedEvent event) {
        World world = event.getWorld();

        MARKERS.add(Marker.circle(Point.of(-100, 100), 20));
        MARKERS.add(Marker.ellipse(Point.of(100, 100), Vector.of(10, 20), 45D));
        MARKERS.add(Marker.icon(Point.of(150, 50), ICON_KEY, 16));
        MARKERS.add(Marker.multiPolygon()
                .setOptions(Options.builder()
                        .strokeColor(0xFFFF0000)
                        .build()
                )
                .clearPolygons()
                .addPolygon(Polygon.of(
                        Polyline.of(
                                Point.of(-200, 200),
                                Point.of(-150, 200),
                                Point.of(-175, 150)
                        ),
                        Polyline.of(
                                Point.of(-300, 200),
                                Point.of(-250, 200),
                                Point.of(-275, 150)
                        ),
                        Polyline.of(
                                Point.of(-400, 200),
                                Point.of(-350, 200),
                                Point.of(-375, 150)
                        )
                ))
        );
        MARKERS.add(Marker.multiPolyline(
                Polyline.of(
                        Point.of(0, 200),
                        Point.of(50, 200),
                        Point.of(25, 150)
                ).loop(),
                Polyline.of(
                        Point.of(100, 200),
                        Point.of(150, 200),
                        Point.of(125, 150)
                ) // no loop
        ));
        MARKERS.add(Marker.polyline(
                Point.of(0, 0),
                Point.of(50, 50),
                Point.of(100, 0)
        ).loop());
        MARKERS.add(Marker.polygon(Polyline.of(
                Point.of(50, -50),
                Point.of(100, -50),
                Point.of(100, -100),
                Point.of(75, -130),
                Point.of(50, -100)
        )));
        MARKERS.add(Marker.rectangle(Point.of(-50, -50), Point.of(-20, 0)));

        world.getLayerRegistry().register(new SimpleLayer(LAYER_KEY, () -> "Marker Test") {
                    @Override
                    public @NotNull Collection<Marker<?>> getMarkers() {
                        return MARKERS;
                    }
                }
                        .setUpdateInterval(60)
                        .setPriority(999)
        );
    }

    @EventHandler
    public void on(WorldUnloadedEvent event) {
        World world = event.getWorld();
        world.getLayerRegistry().unregister(LAYER_KEY);
        Pl3xMap.api().getIconRegistry().unregister(ICON_KEY);
    }
}
