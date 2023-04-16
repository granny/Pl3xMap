package net.pl3x.map.fabric;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Map;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Pl3xMapImpl extends Pl3xMap {
    private final Pl3xMapFabric mod;

    public Pl3xMapImpl(Pl3xMapFabric mod) {
        super();

        this.mod = mod;

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
        return Path.of("config", "pl3xmap");
    }

    @Override
    public int getColorForPower(byte power) {
        return RedStoneWireBlock.getColorForPower(power);
    }

    @Override
    public void loadBlocks() {
        for (Map.Entry<ResourceKey<Block>, Block> entry : this.mod.getServer().registryAccess().registryOrThrow(Registries.BLOCK).entrySet()) {
            getBlockRegistry().register(entry.getKey().location().toString(), entry.getValue().defaultMaterialColor().col);
        }
    }

    @Override
    public void loadWorlds() {
        this.mod.getServer().getAllLevels().forEach(level -> {
            String name = level.dimension().location().toString();
            WorldConfig worldConfig = new WorldConfig(name);
            if (worldConfig.ENABLED) {
                getWorldRegistry().register(new FabricWorld(level, name, worldConfig));
            }
        });
    }

    @Override
    public void loadPlayers() {
        // todo
        //Bukkit.getOnlinePlayers().forEach(getPlayerRegistry()::register);
    }
}
