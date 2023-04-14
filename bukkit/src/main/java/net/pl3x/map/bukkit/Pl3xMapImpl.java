package net.pl3x.map.bukkit;

import java.lang.reflect.Field;
import java.nio.file.Path;
import net.minecraft.server.level.ServerLevel;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.task.RenderWorld;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R3.CraftWorld;
import org.bukkit.plugin.java.JavaPlugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Pl3xMapImpl extends Pl3xMap {
    JavaPlugin plugin;

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
    public void enable() {
        super.enable();

        // todo remove temp stuff below

        // initialize a fullrender
        Bukkit.getScheduler().runTaskLater(this.plugin, () -> {
            RenderWorld render = new RenderWorld(getWorldRegistry().get("world"));
            render.run();
        }, 20 * 10);
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
