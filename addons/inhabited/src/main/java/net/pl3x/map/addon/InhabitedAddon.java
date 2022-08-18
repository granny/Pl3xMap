package net.pl3x.map.addon;

import net.pl3x.map.addon.renderer.InhabitedRenderer;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.addon.Addon;
import net.pl3x.map.render.task.RendererRegistry;

public class InhabitedAddon extends Addon {
    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.register("inhabited", InhabitedRenderer.class);
    }

    @Override
    public void onDisable() {
        // unregister our custom renderer from Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.unregister("inhabited");
    }
}
