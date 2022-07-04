package net.pl3x.map.configuration;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.util.FileUtil;
import org.bukkit.World;

public class WorldConfig extends AbstractConfig {
    @Key("enabled")
    @Comment("Enables this world to be rendered on the map.")
    public boolean ENABLED = false;

    @Key("render.scanners")
    @Comment("""
            Scanner types to use. Each scanner will render a different
            type of map. The built in scanners include:
            basic, biome, nether""")
    public List<String> RENDER_SCANNERS = new ArrayList<>() {{
        add("basic");
    }};

    @Key("render.background.interval")
    @Comment("""
            How often to check the queue for any chunks needing updates.
            This is what updates your map as changes happen in the world.
            Use 0 or negative value to disable this feature""")
    public int RENDER_BACKGROUND_INTERVAL = 5;
    @Key("render.background.max-chunks-per-interval")
    @Comment("""
            The maximum amount of chunks to render from the queue at
            time. Setting this too high may cause lag on your main
            thread if the queue gets too large""")
    public int RENDER_BACKGROUND_MAX_CHUNKS_PER_INTERVAL = 1024;
    @Key("render.background.render-threads")
    @Comment("""
            This number of threads to use for background rendering.
            Value of -1 will use 50% of the available cores.""")
    public int RENDER_BACKGROUND_RENDER_THREADS = -1;

    @Key("render.biome-blend")
    @Comment("""
            Enables blending of biome grass/foliage/water colors
            similar to the client's biome blending option.
            Use 0 or negative value to disable this feature.
            Note: This may slow down your renders quite
            drastically if enabled""")
    public int RENDER_BIOME_BLEND = 0;

    @Key("render.translucent-fluids")
    @Comment("""
            Enable translucent water.
            This will make the water look fancier and translucent
            so you can see the ground below in shallow waters.""")
    public boolean RENDER_TRANSLUCENT_FLUIDS = true;

    @Key("render.translucent-glass")
    @Comment("""
            Enable translucent glass.
            This will make the glass look fancier and translucent
            so you can see the ground below.""")
    public boolean RENDER_TRANSLUCENT_GLASS = true;

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
        if (getConfig().get("world-settings.default." + path) == null) {
            getConfig().set("world-settings.default." + path, def);
        }
        return getConfig().get("world-settings." + this.world.getName() + "." + path,
                getConfig().get("world-settings.default." + path));
    }

    @Override
    protected void setComments(String path, List<String> comments) {
        getConfig().setComments("world-settings.default." + path, comments);
    }
}
