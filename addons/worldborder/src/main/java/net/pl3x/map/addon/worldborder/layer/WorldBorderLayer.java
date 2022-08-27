package net.pl3x.map.addon.worldborder.layer;

import java.util.Collection;
import java.util.Collections;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.Point;
import net.pl3x.map.api.marker.layer.Layer;
import net.pl3x.map.api.marker.type.Polyline;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.jetbrains.annotations.NotNull;

public class WorldBorderLayer implements Layer {
    private final MapWorld mapWorld;

    private final String label;
    private final boolean showControls;
    private final boolean defaultHidden;
    private final int priority;
    private final int zIndex;

    public WorldBorderLayer(MapWorld mapWorld) {
        this.mapWorld = mapWorld;

        this.label = "World Border";
        this.showControls = true;
        this.defaultHidden = false;
        this.priority = 0;
        this.zIndex = 500;
    }

    public MapWorld getMapWorld() {
        return this.mapWorld;
    }

    @Override
    public String getLabel() {
        return this.label;
    }

    @Override
    public boolean showControls() {
        return this.showControls;
    }

    @Override
    public boolean defaultHidden() {
        return this.defaultHidden;
    }

    @Override
    public int getPriority() {
        return this.priority;
    }

    @Override
    public int getZIndex() {
        return this.zIndex;
    }

    @Override
    @NotNull
    public Collection<Marker> getMarkers() {
        WorldBorder border = this.mapWorld.getWorld().getWorldBorder();
        Location center = border.getCenter();
        int x = center.getBlockX();
        int z = center.getBlockZ();
        int r = (int) border.getSize() / 2;
        Polyline.Line line = new Polyline.Line()
                .addPoint(new Point(x - r, z - r))
                .addPoint(new Point(x + r, z - r))
                .addPoint(new Point(x + r, z + r))
                .addPoint(new Point(x - r, z + r))
                .addPoint(new Point(x - r, z - r));
        return Collections.singletonList(new Polyline().addLine(line));
    }
}
