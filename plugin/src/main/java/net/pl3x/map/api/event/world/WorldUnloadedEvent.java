package net.pl3x.map.api.event.world;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.api.event.RegisteredHandler;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;

public class WorldUnloadedEvent extends WorldEvent {
    private static final List<RegisteredHandler> handlers = new ArrayList<>();

    public WorldUnloadedEvent(@NotNull MapWorld mapWorld) {
        super(mapWorld);
    }

    @NotNull
    public List<RegisteredHandler> getHandlers() {
        return handlers;
    }
}
