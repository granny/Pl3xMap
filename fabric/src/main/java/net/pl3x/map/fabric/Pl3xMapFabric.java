package net.pl3x.map.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.pl3x.map.core.Pl3xMap;

public class Pl3xMapFabric implements DedicatedServerModInitializer {
    private final Pl3xMap pl3xmap;

    private MinecraftServer server;

    public Pl3xMapFabric() {
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onInitializeServer() {
        new FabricPlayerListener();

        ServerTickEvents.END_SERVER_TICK.register(server -> this.pl3xmap.getScheduler().tick());

        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;
            this.pl3xmap.enable();
        });

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> {
            this.pl3xmap.disable();

            this.pl3xmap.getBlockRegistry().unregister();
        });
    }

    public MinecraftServer getServer() {
        return this.server;
    }
}
