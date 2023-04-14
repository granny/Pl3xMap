package net.pl3x.map.bukkit;

import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.core.Pl3xMap;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xMapBukkit extends JavaPlugin {
    private final Pl3xMap pl3xmap;

    public Pl3xMapBukkit() {
        super();
        this.pl3xmap = new Pl3xMapImpl(this);
    }

    @Override
    public void onLoad() {
        // register blocks
        for (Map.Entry<ResourceKey<Block>, Block> entry : MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.BLOCK).entrySet()) {
            this.pl3xmap.getBlockRegistry().register(entry.getKey().location().toString(), entry.getValue().defaultMaterialColor().col);
        }
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
