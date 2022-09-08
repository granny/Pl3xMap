package net.pl3x.map.addon.worldborder.layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import net.pl3x.map.Key;
import net.pl3x.map.addon.worldborder.border.Border;
import net.pl3x.map.addon.worldborder.border.BorderType;
import net.pl3x.map.markers.layer.Layer;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.markers.option.Fill;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.markers.option.Stroke;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public class WorldBorderLayer implements Layer {
    public static final Key KEY = new Key("world-border");
    public static final List<Marker> EMPTY_LIST = new ArrayList<>();

    private final World world;
    private final String label;
    private final int updateInterval;
    private final boolean showControls;
    private final boolean defaultHidden;
    private final int priority;
    private final int zIndex;

    private Border border;

    public WorldBorderLayer(@NotNull World world) {
        this.world = world;
        this.label = "World Border";
        this.updateInterval = 15;
        this.showControls = true;
        this.defaultHidden = false;
        this.priority = 0;
        this.zIndex = 500;
    }

    @NotNull
    public World getWorld() {
        return this.world;
    }

    @NotNull
    public Border getBorder() {
        BorderType type = BorderType.get();
        if (this.border == null || type != this.border.getType()) {
            this.border = type.create(getWorld());
        }
        return this.border;
    }

    public void clearBorder() {
        this.border = null;
    }

    @Override
    @NotNull
    public Key getKey() {
        return KEY;
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
        if (marker == null) {
            // we cannot use Collections.emptyList() because it
            // is a private internal class and GSON will throw
            // warnings when the object is serialized.
            return EMPTY_LIST;
        }
        marker.setOptions(new Options()
                .setStroke(new Stroke().setColor(0xFFFF0000))
                .setFill(new Fill().setEnabled(false))
        );
        return Collections.singletonList(marker);
    }
}
