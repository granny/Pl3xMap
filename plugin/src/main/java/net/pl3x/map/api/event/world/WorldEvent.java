package net.pl3x.map.api.event.world;

import net.pl3x.map.api.event.Event;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;

public abstract class WorldEvent extends Event {
    private final MapWorld mapWorld;

    public WorldEvent(@NotNull MapWorld mapWorld) {
        this.mapWorld = mapWorld;
    }

    @NotNull
    public MapWorld getWorld() {
        return this.mapWorld;
    }
}
