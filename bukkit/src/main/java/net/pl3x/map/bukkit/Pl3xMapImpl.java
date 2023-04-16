package net.pl3x.map.bukkit;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Pl3xMapImpl extends Pl3xMap {
    private final JavaPlugin plugin;

    public Pl3xMapImpl(JavaPlugin plugin) {
        super();

        this.plugin = plugin;

        try {
            Field api = Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }

        init();
    }

    @Override
    @NonNull
    public Path getMainDir() {
        return this.plugin.getDataFolder().toPath();
    }

    @Override
    public int getColorForPower(byte power) {
        return RedStoneWireBlock.getColorForPower(power);
    }

    @Override
    public void loadBlocks() {
        for (Map.Entry<ResourceKey<Block>, Block> entry : MinecraftServer.getServer().registryAccess().registryOrThrow(Registries.BLOCK).entrySet()) {
            getBlockRegistry().register(entry.getKey().location().toString(), entry.getValue().defaultMaterialColor().col);
        }
    }

    @Override
    public void loadWorlds() {
        Bukkit.getWorlds().forEach(world -> {
            ServerLevel level = ((CraftWorld) world).getHandle();
            WorldConfig worldConfig = new WorldConfig(world.getName());
            if (worldConfig.ENABLED) {
                getWorldRegistry().register(new BukkitWorld(level, world.getName(), worldConfig));
            }
        });
    }

    @Override
    public void loadPlayers() {
        // todo
        //Bukkit.getOnlinePlayers().forEach(getPlayerRegistry()::register);
    }
}
