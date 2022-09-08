package net.pl3x.map.event.world;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.event.RegisteredHandler;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public class WorldLoadedEvent extends WorldEvent {
    private static final List<RegisteredHandler> handlers = new ArrayList<>();

    public WorldLoadedEvent(@NotNull World world) {
        super(world);
    }

    @NotNull
    public List<RegisteredHandler> getHandlers() {
        return handlers;
    }
}
