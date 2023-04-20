package net.pl3x.map.fabric;

import net.fabricmc.api.DedicatedServerModInitializer;

public class Pl3xMapFabric implements DedicatedServerModInitializer {
    public Pl3xMapFabric() {
        new Pl3xMapImpl();
    }

    @Override
    public void onInitializeServer() {
    }
}
