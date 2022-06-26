package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;
import org.bukkit.World;

public class WorldConfig extends AbstractConfig {
    @Key("enabled")
    public boolean ENABLED = false;

    @Key("render.background.enabled")
    public boolean RENDER_BACKGROUND_ENABLED = true;

    @Key("render.layer.blocks.enabled")
    public boolean RENDER_LAYER_BLOCKS = true;
    @Key("render.layer.blocks.biome-blend")
    public int RENDER_BLOCKS_BIOME_BLEND = 0;

    @Key("render.layer.biomes.enabled")
    public boolean RENDER_LAYER_BIOMES = true;

    @Key("render.layer.heights.enabled")
    public boolean RENDER_LAYER_HEIGHTS = true;

    @Key("render.layer.fluids.enabled")
    public boolean RENDER_LAYER_FLUIDS = true;
    @Key("render.layer.fluids.translucent")
    public boolean RENDER_FLUIDS_TRANSLUCENT = true;

    @Key("zoom.default")
    public int ZOOM_DEFAULT = 0;
    @Key("zoom.max-out")
    public int ZOOM_MAX_OUT = 3;
    @Key("zoom.max-in")
    public int ZOOM_MAX_IN = 2;

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
