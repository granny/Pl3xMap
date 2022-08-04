package net.pl3x.map.configuration;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.api.heightmap.Heightmap;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.util.Mathf;
import org.bukkit.World;

public class WorldConfig extends AbstractConfig {
    @Key("enabled")
    @Comment("Enables this world to be rendered on the map.")
    public boolean ENABLED = false;

    public String DISPLAY_NAME = "<world>";
    public String ICON = "";
    public int ORDER = 0;

    @Key("render.renderers.visible")
    @Comment("""
            Renderer types to use. Each scanner will render a different
            type of map. The built in scanners include:
            basic, biomes""")
    public List<String> RENDERERS_VISIBLE = new ArrayList<>() {{
        add("basic");
        add("biomes");
    }};

    @Key("render.renderers.hidden")
    @Comment("""
            Renderer types to use, but _not_ show on the map.""")
    public List<String> RENDERERS_HIDDEN = new ArrayList<>() {{
        add("blockinfo");
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
            Enables blending of biome grass/foliage/water colors similar to
            the client's biome blending option.
            Note: This may slow down your renders quite drastically if enabled.
            0 = off
            1 = 3x3
            2 = 5x5 (default)
            3 = 7x7
            4 = 9x9
            5 = 11x11
            6 = 13x13
            7 = 15x15""")
    public int RENDER_BIOME_BLEND = 2;

    @Key("render.skylight")
    @Comment("""
            Skylight value for world.
            Values are 0-15 with 1 being darkest and 15 being full bright.
            Negative values disable skylight (same as full bright).""")
    public int RENDER_SKYLIGHT = 5;

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

    @Key("render.heightmap-type")
    @Comment("""
            Type of heightmap to render.
            MODERN is a clearer, more detailed view.
            OLD_SCHOOL is the type from V1.
            DYNMAP makes every other Y layer darker.""")
    public Heightmap.Type RENDER_HEIGHTMAP_TYPE = Heightmap.Type.MODERN;

    @Key("ui.coords")
    @Comment("Shows the coordinates box on the map")
    public boolean UI_COORDS = true;
    @Key("ui.link")
    @Comment("Shows the link box on the map")
    public boolean UI_LINK = true;

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

    @Key("markers.update-interval")
    @Comment("How often (in seconds) to update map markers")
    public int MARKERS_UPDATE_INTERVAL = 5;

    @Key("player-tracker.enabled")
    @Comment("Enable the player tracker")
    public boolean PLAYER_TRACKER_ENABLED = true;
    @Key("player-tracker.interval")
    @Comment("How often (in seconds) to update the player tracker")
    public int PLAYER_TRACKER_INTERVAL = 1;
    @Key("player-tracker.controls.show")
    @Comment("Should the player tracker control be shown")
    public boolean PLAYER_TRACKER_SHOW_CONTROLS = true;
    @Key("player-tracker.controls.hidden")
    @Comment("Should the player layer be hidden by default")
    public boolean PLAYER_TRACKER_DEFAULT_HIDDEN = false;
    @Key("player-tracker.layer.priority")
    @Comment("The priority of the player tracker layer")
    public int PLAYER_TRACKER_PRIORITY = 2;
    @Key("player-tracker.layer.z-index")
    @Comment("The z-index of the player tracker layer")
    public int PLAYER_TRACKER_Z_INDEX = 2;
    @Key("player-tracker.hide-players.spectators")
    @Comment("Hide spectators from the map")
    public boolean PLAYER_TRACKER_HIDE_SPECTATORS = true;
    @Key("player-tracker.hide-players.invisible")
    @Comment("Hide invisible players from the map")
    public boolean PLAYER_TRACKER_HIDE_INVISIBLE = true;
    @Key("player-tracker.nameplate.enabled")
    @Comment("Show players' nameplate by their icon")
    public boolean PLAYER_TRACKER_NAMEPLATE_ENABLED = true;
    @Key("player-tracker.nameplate.show-head")
    @Comment("Show players' head in the nameplate")
    public boolean PLAYER_TRACKER_NAMEPLATE_SHOW_HEAD = true;
    @Key("player-tracker.nameplate.show-armor")
    @Comment("Show players' armor in the nameplate")
    public boolean PLAYER_TRACKER_NAMEPLATE_SHOW_ARMOR = true;
    @Key("player-tracker.nameplate.show-health")
    @Comment("Show players' health in the nameplate")
    public boolean PLAYER_TRACKER_NAMEPLATE_SHOW_HEALTH = true;
    @Key("player-tracker.nameplate.head-url")
    @Comment("Head url for showing heads")
    public String PLAYER_TRACKER_NAMEPLATE_HEADS_URL = "https://mc-heads.net/avatar/{uuid}/16";

    private final World world;

    public WorldConfig(World world) {
        this.world = world;
    }

    public void reload() {
        reload(FileUtil.PLUGIN_DIR.resolve("worlds.yml"), WorldConfig.class);

        RENDER_BIOME_BLEND = Mathf.clamp(0, 7, RENDER_BIOME_BLEND);
    }

    @Override
    protected Object getClassObject() {
        return this;
    }

    @Override
    protected Object getValue(String path, Object def) {
        boolean heightmap = def instanceof Heightmap.Type;
        if (heightmap) {
            def = ((Heightmap.Type) def).name();
        }

        if (getConfig().get("world-settings.default." + path) == null) {
            getConfig().set("world-settings.default." + path, def);
        }

        Object value = getConfig().get("world-settings." + this.world.getName() + "." + path,
                getConfig().get("world-settings.default." + path));

        if (value instanceof String && heightmap) {
            value = Heightmap.Type.get((String) value);
        }

        return value;
    }

    @Override
    protected void setComments(String path, List<String> comments) {
        getConfig().setComments("world-settings.default." + path, comments);
    }
}
