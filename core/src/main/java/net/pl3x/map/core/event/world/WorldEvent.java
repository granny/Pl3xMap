package net.pl3x.map.core.event.world;

import net.pl3x.map.core.event.Event;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class WorldEvent extends Event {
    private final World world;

    public WorldEvent(@NonNull World world) {
        this.world = world;
    }

    public @NonNull World getWorld() {
        return this.world;
    }
}
