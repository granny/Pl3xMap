package net.pl3x.map.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.MinecraftServer;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.player.PlayerRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Pl3xMapFabric implements DedicatedServerModInitializer {
    private final Pl3xMap pl3xmap;

    private MinecraftServer server;
    private PlayerListener playerListener;

    public Pl3xMapFabric() {
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onInitializeServer() {
        this.playerListener = new PlayerListener() {
        };

        ServerTickEvents.END_SERVER_TICK.register(server -> this.pl3xmap.getScheduler().tick());

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
            this.playerListener.onJoin(registry.register(handler.getPlayer().getUUID().toString(), new FabricPlayer(handler.getPlayer())));
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
            this.playerListener.onQuit(registry.unregister(handler.getPlayer().getUUID().toString()));
        });

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.pl3xmap.enable();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            this.pl3xmap.disable();
            this.pl3xmap.getBlockRegistry().unregister();
        });
    }

    @NonNull
    public MinecraftServer getServer() {
        return this.server;
    }
}
