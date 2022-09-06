package net.pl3x.map.addon.worldborder;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.addon.worldborder.listener.PluginListener;
import net.pl3x.map.addon.worldborder.listener.WorldListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class WorldBorderAddon extends Addon {
    @Override
    public void onEnable() {
        PluginListener pluginListener = new PluginListener();
        WorldListener worldListener = new WorldListener();

        Plugin pl3xMap = Bukkit.getPluginManager().getPlugin("Pl3xMap");

        Bukkit.getPluginManager().registerEvents(pluginListener, pl3xMap);
        Bukkit.getPluginManager().registerEvents(worldListener, pl3xMap);
        Pl3xMap.api().getEventRegistry().register(worldListener, this);
    }
}
