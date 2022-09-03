package net.pl3x.map.addon.worldborder;

import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.addon.worldborder.listener.PluginListener;
import net.pl3x.map.addon.worldborder.listener.WorldListener;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.addon.Addon;
import org.bukkit.Bukkit;

public class WorldBorderAddon extends Addon {
    @Override
    public void onEnable() {
        PluginListener pluginListener = new PluginListener();
        WorldListener worldListener = new WorldListener();

        Bukkit.getPluginManager().registerEvents(pluginListener, Pl3xMapPlugin.getInstance());
        Bukkit.getPluginManager().registerEvents(worldListener, Pl3xMapPlugin.getInstance());
        Pl3xMap.api().getEventRegistry().register(worldListener, this);
    }
}
