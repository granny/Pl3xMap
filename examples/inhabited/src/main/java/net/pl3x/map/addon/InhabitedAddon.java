package net.pl3x.map.addon;

import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.addon.Addon;

public class InhabitedAddon extends Addon {
    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        Pl3xMap.api().getRendererManager().register("inhabited", InhabitedRenderer.class);
    }

    @Override
    public void onDisable() {
        // register our custom renderer with Pl3xMap
        Pl3xMap.api().getRendererManager().unregister("inhabited");
    }
}
