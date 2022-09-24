package net.pl3x.map.addon.griefprevention;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.addon.griefprevention.configuration.Config;
import net.pl3x.map.addon.griefprevention.hook.GPBukkitHook;
import net.pl3x.map.addon.griefprevention.hook.GPHook;
import net.pl3x.map.addon.griefprevention.listener.WorldListener;

public class GriefPrevention extends Addon {
    private GPHook gpHook;

    @Override
    public void onEnable() {
        this.gpHook = switch (Pl3xMap.api().getImpl()) {
            case PAPER -> new GPBukkitHook();
        };

        Config.reload();

        Pl3xMap.api().getEventRegistry().register(new WorldListener(this));
    }

    public GPHook getGPHook() {
        return this.gpHook;
    }
}
