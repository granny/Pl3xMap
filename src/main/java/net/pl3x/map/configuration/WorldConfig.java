package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;
import org.bukkit.World;

import java.util.List;

public class WorldConfig extends AbstractConfig {
    @Key("enabled")
    @Comment("Enables this world to be rendered on the map.")
    public boolean ENABLED = false;

    @Key("render.background.interval")
    @Comment("""
            How often to run the background render task for this world.
            This is what updates your map as changes happen in the world.
            Use 0 or negative value to disable this feature""")
    public int RENDER_BACKGROUND_INTERVAL = 5;

    @Key("render.layer.blocks.enabled")
    @Comment("""
            Render and show the block layer on the map.
            Each pixel/color represents a block in game.
            Note: You must have at blocks and/or biome layers
            at a minimum for the map to work correctly""")
    public boolean RENDER_LAYER_BLOCKS = true;
    @Key("render.layer.blocks.biome-blend")
    @Comment("""
            Enables blending of biome grass/foliage/water colors
            similar to the client's biome blending option.
            Use 0 or negative value to disable this feature.
            Note: This may slow down your renders quite
            drastically if enabled""")
    public int RENDER_BLOCKS_BIOME_BLEND = 0;

    @Key("render.layer.biomes.enabled")
    @Comment("""
            Render and show the biomes layer on the map.
            Each color represents a biome in game.""")
    public boolean RENDER_LAYER_BIOMES = true;

    @Key("render.layer.heights.enabled")
    @Comment("""
            Enable the heightmap layer.
            This adds a transparent layer of darkened pixels
            that represent the elevation of the land.""")
    public boolean RENDER_LAYER_HEIGHTS = true;

    @Key("render.layer.fluids.enabled")
    @Comment("""
            Enable the fluid layer.
            This adds a transparent layer of water/lava in your world""")
    public boolean RENDER_LAYER_FLUIDS = true;
    @Key("render.layer.fluids.translucent")
    @Comment("""
            Enable translucent water.
            This will make the water look fancier and translucent
            so you can see the ground below in shallow waters.""")
    public boolean RENDER_FLUIDS_TRANSLUCENT = true;

    @Key("zoom.default")
    @Comment("""
            The default zoom when loading the map in browser.
            Normal sized tiles (1 pixel = 1 block) are
            always at zoom level 0.""")
    public int ZOOM_DEFAULT = 0;
    @Key("zoom.max-out")
    @Comment("""
            The maximum zoom out you can do on the map.
            Each additional level requires a new set of tiles
            to be rendered, so don't go wild here.""")
    public int ZOOM_MAX_OUT = 3;
    @Key("zoom.max-in")
    @Comment("""
            Extra zoom in layers will stretch the original
            tile images so you can zoom in further without
            the extra cost of rendering more tiles.""")
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

    @Override
    protected void setComments(String path, List<String> comments) {
        getConfig().setComments("world-settings.default." + path, comments);
    }
}
