package net.pl3x.map.addon.worldborder.layer;

import java.util.Collection;
import java.util.Collections;
import net.pl3x.map.addon.worldborder.border.Border;
import net.pl3x.map.addon.worldborder.border.BorderType;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.api.markers.layer.Layer;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;

public class WorldBorderLayer implements Layer {
    private final MapWorld mapWorld;
    private final Key key;
    private final String label;
    private final int updateInterval;
    private final boolean showControls;
    private final boolean defaultHidden;
    private final int priority;
    private final int zIndex;

    private Border border;

    public WorldBorderLayer(@NotNull MapWorld mapWorld) {
        this.mapWorld = mapWorld;
        this.key = new Key("world-border");
        this.label = "World Border";
        this.updateInterval = 15;
        this.showControls = true;
        this.defaultHidden = false;
        this.priority = 0;
        this.zIndex = 500;
    }

    @NotNull
    public MapWorld getMapWorld() {
        return this.mapWorld;
    }

    @NotNull
    public Border getBorder() {
        BorderType type = BorderType.get();
        if (this.border == null || type != this.border.getType()) {
            this.border = type.create(getMapWorld());
        }
        return this.border;
    }

    @Override
    @NotNull
    public Key getKey() {
        return this.key;
    }

    @Override
    @NotNull
    public String getLabel() {
        return this.label;
    }

    @Override
    public int getUpdateInterval() {
        return this.updateInterval;
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
        Marker marker = getBorder().getMarker();
        return marker == null ? Collections.emptyList() : Collections.singletonList(marker);
    }
}
