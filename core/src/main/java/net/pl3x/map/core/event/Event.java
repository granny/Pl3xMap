package net.pl3x.map.core.event;

import java.util.List;
import net.pl3x.map.core.Pl3xMap;
import org.checkerframework.checker.nullness.qual.NonNull;

public abstract class Event {
    public void callEvent() {
        Pl3xMap.api().getEventRegistry().callEvent(this);
    }

    public abstract @NonNull List<@NonNull RegisteredHandler> getHandlers();
}
