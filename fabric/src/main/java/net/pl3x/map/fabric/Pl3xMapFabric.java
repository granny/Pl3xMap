package net.pl3x.map.fabric;

import java.util.Map;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.core.Pl3xMap;

public class Pl3xMapFabric implements DedicatedServerModInitializer {
    private final Pl3xMap pl3xmap;

    private MinecraftServer server;

    public Pl3xMapFabric() {
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            this.server = server;

            for (Map.Entry<ResourceKey<Block>, Block> entry : server.registryAccess().registryOrThrow(Registries.BLOCK).entrySet()) {
                this.pl3xmap.getBlockRegistry().register(entry.getKey().location().toString(), entry.getValue().defaultMaterialColor().col);
            }

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
