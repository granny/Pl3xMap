package net.pl3x.map.addon.worldborder.border;

import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Border {
    private final MapWorld mapWorld;
    private final BorderType type;

    protected Marker marker;

    public Border(@NotNull MapWorld mapWorld, BorderType type) {
        this.mapWorld = mapWorld;
        this.type = type;

        update();
    }

    @NotNull
    public MapWorld getMapWorld() {
        return this.mapWorld;
    }

    @NotNull
    public BorderType getType() {
        return this.type;
    }

    @Nullable
    public Marker getMarker() {
        return this.marker;
    }

    public abstract void update();
}
