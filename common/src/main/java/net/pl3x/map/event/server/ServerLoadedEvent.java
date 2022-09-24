package net.pl3x.map.event.server;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.event.Event;
import net.pl3x.map.event.RegisteredHandler;
import org.jetbrains.annotations.NotNull;

public class ServerLoadedEvent extends Event {
    private static final List<RegisteredHandler> handlers = new ArrayList<>();

    private final Type type;

    public ServerLoadedEvent(@NotNull Type type) {
        this.type = type;
    }

    @NotNull
    public Type getType() {
        return this.type;
    }

    @Override
    @NotNull
    public List<RegisteredHandler> getHandlers() {
        return handlers;
    }

    public enum Type {
        STARTUP,
        RELOAD
    }
}
