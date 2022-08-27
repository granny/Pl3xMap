package net.pl3x.map.addon.worldborder;

import net.pl3x.map.addon.worldborder.layer.WorldBorderLayer;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.addon.Addon;
import net.pl3x.map.api.registry.LayerRegistry;

public class WorldBorderAddon extends Addon {
    private final Key key = new Key("world-border");

    @Override
    public void onEnable() {
        LayerRegistry registry = Pl3xMap.api().getLayerRegistry();
        //registry.register(this.key, new WorldBorderLayer());
    }

    @Override
    public void onDisable() {
        LayerRegistry registry = Pl3xMap.api().getLayerRegistry();
        registry.unregister(this.key);
    }
}
