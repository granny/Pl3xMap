package net.pl3x.map.addon.flowermap;

import net.pl3x.map.addon.flowermap.renderer.FlowerMapRenderer;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.addon.Addon;
import net.pl3x.map.render.task.RendererRegistry;

public class FlowerMapAddon extends Addon {
    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.register("flowermap", FlowerMapRenderer.class);
    }

    @Override
    public void onDisable() {
        // unregister our custom renderer from Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.unregister("flowermap");
    }
}
