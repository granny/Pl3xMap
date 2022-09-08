package net.pl3x.map.addon.worldborder.border;

import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Border {
    private final World world;
    private final BorderType type;

    protected Marker marker;

    public Border(@NotNull World world, BorderType type) {
        this.world = world;
        this.type = type;

        update();
    }

    @NotNull
    public World getWorld() {
        return this.world;
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
