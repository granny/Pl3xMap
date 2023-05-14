package net.pl3x.map.core.event.server;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.core.event.Event;
import net.pl3x.map.core.event.RegisteredHandler;
import org.jetbrains.annotations.NotNull;

public class Pl3xMapEnabledEvent extends Event {
    private static final List<@NotNull RegisteredHandler> handlers = new ArrayList<>();

    @Override
    public @NotNull List<@NotNull RegisteredHandler> getHandlers() {
        return handlers;
    }
}
