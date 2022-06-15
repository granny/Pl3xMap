package net.pl3x.map.configuration;

import org.bukkit.World;

import java.nio.file.Files;
import java.nio.file.Path;

public class WorldConfig extends AbstractConfig {
    @Key("render.background.enabled")
    public boolean RENDER_BACKGROUND_ENABLED = true;

    private final String worldName;

    public WorldConfig(World world) {
        this.worldName = world.getName();
    }

    public void reload() {
        Path path = WORLD_DIR.resolve(worldName + ".yml");
        if (!Files.exists(path)) {
            throw new RuntimeException();
        }
        reload(path, WorldConfig.class);
    }

    @Override
    protected Object getClassObject() {
        return this;
    }
}
