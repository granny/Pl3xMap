package net.pl3x.map.addon.webp;

import net.pl3x.map.addon.webp.io.Webp;
import net.pl3x.map.api.addon.Addon;
import net.pl3x.map.api.image.io.IO;

public class WebpAddon extends Addon {
    @Override
    public void onEnable() {
        IO.register("webp", new Webp());
    }

    @Override
    public void onDisable() {
        IO.unregister("webp");
    }
}
