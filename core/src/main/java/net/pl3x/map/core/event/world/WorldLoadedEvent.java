package net.pl3x.map.core.event.world;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.core.event.RegisteredHandler;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class WorldLoadedEvent extends WorldEvent {
    private static final List<@NonNull RegisteredHandler> handlers = new ArrayList<>();

    public WorldLoadedEvent(@NonNull World world) {
        super(world);
    }

    public @NonNull List<@NonNull RegisteredHandler> getHandlers() {
        return handlers;
    }
}
