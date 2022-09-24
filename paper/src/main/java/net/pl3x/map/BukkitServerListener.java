package net.pl3x.map;

import net.pl3x.map.event.server.ServerLoadedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

public class BukkitServerListener implements ServerListener, Listener {
    @EventHandler
    public void onServerLoaded(ServerLoadEvent event) {
        ServerLoadedEvent.Type type = switch (event.getType()) {
            case STARTUP -> ServerLoadedEvent.Type.STARTUP;
            case RELOAD -> ServerLoadedEvent.Type.RELOAD;
        };
        Pl3xMap.api().getEventRegistry().callEvent(new ServerLoadedEvent(type));
    }
}
