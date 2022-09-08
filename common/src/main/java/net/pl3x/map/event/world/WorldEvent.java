package net.pl3x.map.event.world;

import net.pl3x.map.event.Event;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class WorldEvent extends Event {
    private final World world;

    public WorldEvent(@NotNull World world) {
        this.world = world;
    }

    @NotNull
    public World getWorld() {
        return this.world;
    }
}
