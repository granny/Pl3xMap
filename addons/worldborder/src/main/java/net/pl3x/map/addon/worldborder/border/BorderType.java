package net.pl3x.map.addon.worldborder.border;

import java.util.function.Function;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;

public enum BorderType {
    CHUNKY(ChunkyBorder::new),
    WORLDBORDER(WBPluginBorder::new),
    VANILLA(VanillaBorder::new);

    private final Function<MapWorld, Border> supplier;

    BorderType(Function<MapWorld, Border> supplier) {
        this.supplier = supplier;
    }

    public Border create(MapWorld mapWorld) {
        return this.supplier.apply(mapWorld);
    }

    public static BorderType get() {
        PluginManager pm = Bukkit.getPluginManager();
        if (pm.isPluginEnabled("ChunkyBorder")) {
            return CHUNKY;
        }
        if (pm.isPluginEnabled("WorldBorder")) {
            return WORLDBORDER;
        }
        return VANILLA;
    }
}
