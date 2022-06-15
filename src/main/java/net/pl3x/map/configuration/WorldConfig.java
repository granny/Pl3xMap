package net.pl3x.map.configuration;

import org.bukkit.World;

public class WorldConfig extends AbstractConfig {
    @Key("render.background.enabled")
    public boolean RENDER_BACKGROUND_ENABLED;

    private final String worldName;

    public WorldConfig(World world) {
        this.worldName = world.getName();
    }

    public void reload() {
        reload(WORLD_DIR.resolve(worldName + ".yml"), WorldConfig.class);
    }

    @Override
    protected String getKeyValue(Key key) {
        return "worlds." + this.worldName + "." + key.value();
    }
}
