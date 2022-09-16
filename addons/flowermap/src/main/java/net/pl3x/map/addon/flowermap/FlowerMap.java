package net.pl3x.map.addon.flowermap;

import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.addon.flowermap.renderer.FlowerMapRenderer;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.RendererRegistry;
import net.pl3x.map.util.FileUtil;

public class FlowerMap extends Addon {
    public static final Key KEY = new Key("flowermap");

    @Override
    public void onEnable() {
        // copy icon to Pl3xMap's icon directory
        FileUtil.extract(getClass(), "flowermap.png", "web/images/icon/", !Config.WEB_DIR_READONLY);

        // register our custom renderer with Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.register(new RendererHolder(KEY, "FlowerMap", FlowerMapRenderer.class));
    }

    @Override
    public void onDisable() {
        // unregister our custom renderer from Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.unregister(KEY);
    }
}
