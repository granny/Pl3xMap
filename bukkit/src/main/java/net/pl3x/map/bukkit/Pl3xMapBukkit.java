package net.pl3x.map.bukkit;

import net.pl3x.map.core.Pl3xMap;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xMapBukkit extends JavaPlugin {
    private final Pl3xMap pl3xmap;

    public Pl3xMapBukkit() {
        super();
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onEnable() {
        this.pl3xmap.enable();
    }

    @Override
    public void onDisable() {
        this.pl3xmap.disable();
    }
}
