package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;
import org.bukkit.World;

import java.util.List;

public class WorldConfig extends AbstractConfig {
    @Key("render.types")
    public List<String> RENDER_TYPES = List.of("basic", "biomes");
    @Key("render.background.enabled")
    public boolean RENDER_BACKGROUND_ENABLED = true;

    private final World world;

    public WorldConfig(World world) {
        this.world = world;
    }

    public void reload() {
        reload(FileUtil.PLUGIN_DIR.resolve("worlds.yml"), WorldConfig.class);
    }

    @Override
    protected Object getClassObject() {
        return this;
    }

    @Override
    protected Object getValue(String path, Object def) {
        getConfig().addDefault("world-settings.default." + path, def);
        return getConfig().get("world-settings." + this.world.getName() + "." + path,
                getConfig().get("world-settings.default." + path));
    }
}
