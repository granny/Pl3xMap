package net.pl3x.map.api.event;

import java.util.List;
import net.pl3x.map.api.Pl3xMap;
import org.jetbrains.annotations.NotNull;

public abstract class Event {
    public void callEvent() {
        Pl3xMap.api().getEventRegistry().callEvent(this);
    }

    @NotNull
    public abstract List<RegisteredHandler> getHandlers();
}
