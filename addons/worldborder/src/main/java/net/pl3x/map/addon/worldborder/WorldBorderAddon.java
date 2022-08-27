package net.pl3x.map.addon.worldborder;

import net.pl3x.map.addon.worldborder.listener.WorldListener;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.addon.Addon;

public class WorldBorderAddon extends Addon {
    @Override
    public void onEnable() {
        Pl3xMap.api().getEventRegistry().register(new WorldListener(), this);
    }
}
