package net.pl3x.map.addon.inhabited;

import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.addon.inhabited.renderer.InhabitedRenderer;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.RendererRegistry;
import net.pl3x.map.util.FileUtil;

public class Inhabited extends Addon {
    public static final Key KEY = Key.of("inhabited");

    @Override
    public void onEnable() {
        // copy icon to Pl3xMap's icon directory
        FileUtil.extract(getClass(), "inhabited.png", "web/images/icon/", !Config.WEB_DIR_READONLY);

        // register our custom renderer with Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.register(new RendererHolder(KEY, "Inhabited", InhabitedRenderer.class));
    }

    @Override
    public void onDisable() {
        // unregister our custom renderer from Pl3xMap
        RendererRegistry registry = Pl3xMap.api().getRendererRegistry();
        registry.unregister(KEY);
    }
}
