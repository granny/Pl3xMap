package net.pl3x.map.fabric;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.task.RenderWorld;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Pl3xMapImpl extends Pl3xMap {
    Pl3xMapFabric mod;

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
    public void enable() {
        super.enable();

        // todo remove temp stuff below

        // initialize a fullrender
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                RenderWorld render = new RenderWorld(getWorldRegistry().get("minecraft:overworld"));
                render.run();
            }
        }, 5000);
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
