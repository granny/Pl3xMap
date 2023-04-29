package net.pl3x.map.core.event.server;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.core.event.Event;
import net.pl3x.map.core.event.RegisteredHandler;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ServerLoadedEvent extends Event {
    private static final List<@NonNull RegisteredHandler> handlers = new ArrayList<>();

    @Override
    public @NonNull List<@NonNull RegisteredHandler> getHandlers() {
        return handlers;
    }
}
