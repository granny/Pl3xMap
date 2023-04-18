package net.pl3x.map.fabric;

import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.player.PlayerRegistry;

public class FabricPlayerListener implements PlayerListener {
    public FabricPlayerListener() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
            onJoin(registry.register(handler.getPlayer().getUUID().toString(), new FabricPlayer(handler.getPlayer())));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
            onQuit(registry.unregister(handler.getPlayer().getUUID().toString()));
        });
    }
}
