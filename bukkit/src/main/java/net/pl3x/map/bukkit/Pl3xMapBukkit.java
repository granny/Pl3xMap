package net.pl3x.map.bukkit;

import net.pl3x.map.core.Pl3xMap;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xMapBukkit extends JavaPlugin {
    private static Pl3xMapBukkit instance;

    public static Pl3xMapBukkit getInstance() {
        return instance;
    }

    private final Pl3xMap pl3xmap;

    private BukkitPlayerListener playerListener;

    public Pl3xMapBukkit() {
        super();
        instance = this;
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onEnable() {
        this.pl3xmap.enable();

        this.playerListener = new BukkitPlayerListener();
        getServer().getPluginManager().registerEvents(this.playerListener, this);
    }

    @Override
    public void onDisable() {
        if (this.playerListener != null) {
            HandlerList.unregisterAll(this.playerListener);
            this.playerListener = null;
        }

        this.pl3xmap.disable();
    }
}
