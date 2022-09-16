package net.pl3x.map.configuration;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.World;

public final class WorldConfig extends AbstractConfig {
    @Key("enabled")
    @Comment("Enables this world to be rendered on the map.")
    public boolean ENABLED = false;

    @Key("render.renderers")
    @Comment("""
            Renderers to use. Each renderer will render a different
            type of map. The built in renderers include: basic, biomes""")
    public List<String> RENDER_RENDERERS = new ArrayList<>() {{
        add("basic");
        add("biomes");
    }};

    @Key("render.render-threads")
    @Comment("""
            The number of threads to use for loading and scanning chunks.
            Value of -1 will use 50% of the available cores. (recommended)""")
    public int RENDER_THREADS = -1;

    @Key("render.biome-blend")
    @Comment("""
            Enables blending of biome grass/foliage/water colors similar to
            the client's biome blending option.
            Note: This may slow down your renders quite drastically if enabled.
            Values are in range 0-7""")
    public int RENDER_BIOME_BLEND = 2;

    @Key("render.skylight")
    @Comment("""
            Skylight value for world.
            Values are 0-15 with 0 being darkest and 15 being full bright.""")
    public int RENDER_SKYLIGHT = 5;

    @Key("render.translucent-fluids")
    @Comment("""
            Enable translucent fluids.
            This will make the fluids look fancier and translucent
            so you can see the blocks below in shallow fluids.""")
    public boolean RENDER_TRANSLUCENT_FLUIDS = true;

    @Key("render.translucent-glass")
    @Comment("""
            Enable translucent glass.
            This will make the glass look fancier and translucent
            so you can see the blocks below.""")
    public boolean RENDER_TRANSLUCENT_GLASS = true;

    @Key("render.heightmap-type")
    @Comment("""
            Type of heightmap to render.
            MODERN is a clearer, more detailed view.
            OLD_SCHOOL is the old type from v1.
            EVEN_ODD makes every other Y layer darker, like Dynmap.""")
    public String RENDER_HEIGHTMAP_TYPE = "MODERN";

    @Key("render.background.interval")
    @Comment("""
            How often to check the queue for any chunks needing updates.
            This is what updates your map as changes happen in the world.
            Setting this too low may cause the background renderer to run
            non-stop. Setting this too high may cause a delay in seeing
            updates on your map. Use 0 value to disable this feature.""")
    public int RENDER_BACKGROUND_INTERVAL = 0;

    @Key("render.background.max-chunks-per-interval")
    @Comment("""
            The maximum amount of regions to render from the queue at
            a time. Setting this too low may cause the queue to become
            quite large and delay updates from showing on the map.
            Setting this too high may cause the background renderer to
            run non-stop.""")
    public int RENDER_BACKGROUND_MAX_REGIONS_PER_INTERVAL = 5;

    @Key("ui.display-name")
    @Comment("The display position for the blockinfo box")
    public String DISPLAY_NAME = "<world>";

    @Key("ui.order")
    @Comment("The order of the world in the world list")
    public int ORDER = 0;

    @Key("ui.blockinfo")
    @Comment("The display position for the blockinfo box")
    public String UI_BLOCKINFO = "bottomleft";

    @Key("ui.attribution")
    @Comment("Shows the footer attributes")
    public boolean UI_ATTRIBUTION = true;

    @Key("ui.coords")
    @Comment("The display position for the coordinates box")
    public String UI_COORDS = "bottomcenter";

    @Key("ui.link")
    @Comment("The display position for the link box")
    public String UI_LINK = "bottomright";

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

    @Key("markers.world.border")
    @Comment("Show the world border on the map")
    public boolean WORLD_BORDER = true;
    @Key("markers.world.spawn")
    @Comment("Show the world spawn on the map")
    public boolean WORLD_SPAWN = true;

    private final World world;

    public WorldConfig(World world) {
        this.world = world;
    }

    public void reload() {
        reload(FileUtil.MAIN_DIR.resolve("config.yml"), WorldConfig.class);

        RENDER_BIOME_BLEND = Mathf.clamp(0, 7, RENDER_BIOME_BLEND);
        RENDER_SKYLIGHT = Mathf.clamp(0, 15, RENDER_SKYLIGHT);
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
    protected void setComment(String path, String comment) {
        getConfig().setComment("world-settings.default." + path, comment);
    }
}
