package net.pl3x.map.core.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.util.Mathf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("CanBeFinal")
public final class WorldConfig extends AbstractConfig {
    @Key("enabled")
    @Comment("Enables this world to be rendered on the map.")
    public boolean ENABLED = false;

    @Key("render.renderers")
    @Comment("""
            Renderers to use. Each renderer will render a different
            type of map. The built in renderers include: basic, biomes, flowermap""")
    public Map<@NonNull String, @NonNull String> RENDER_RENDERERS = new LinkedHashMap<>() {{
        put("basic", "overworld_basic");
        put("biomes", "overworld_biomes");
    }};

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
    public int RENDER_SKYLIGHT = 15;

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
            NONE has no heightmap drawn.
            EVEN_ODD makes every other Y layer darker, like Dynmap.
            LOW_CONTRAST same as MODERN, but lighter.
            MODERN is a clearer, more detailed view.
            OLD_SCHOOL is the old type from v1.
            EVEN_ODD_LOW_CONTRAST mix of EVEN_ODD and LOW_CONTRAST.
            EVEN_ODD_MODERN mix of EVEN_ODD and MODERN.
            EVEN_ODD_OLD_SCHOOL mix of EVEN_ODD and OLD_SCHOOL.""")
    public String RENDER_HEIGHTMAP_TYPE = "MODERN";

    @Key("ui.display-name")
    @Comment("""
            The display name of the world in the world list.
            Use <world> to use the official world name.""")
    public String DISPLAY_NAME = "<world>";

    @Key("ui.order")
    @Comment("The order of the world in the world list")
    public int ORDER = 0;

    @Key("ui.attribution")
    @Comment("Shows the footer attributes")
    public boolean UI_ATTRIBUTION = true;

    @Key("ui.blockinfo")
    @Comment("The display position for the blockinfo box")
    public String UI_BLOCKINFO = "bottomleft";

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

    @Key("markers.spawn.enabled")
    @Comment("Show the world spawn on the map")
    public boolean MARKERS_SPAWN_ENABLED = true;
    @Key("markers.worldborder.enabled")
    @Comment("Show the world border on the map")
    public boolean MARKERS_WORLDBORDER_ENABLED = true;

    private final String worldName;

    public WorldConfig(@NonNull String worldName) {
        this.worldName = worldName;
        reload();
    }

    public void reload() {
        reload(Pl3xMap.api().getMainDir().resolve("config.yml"), WorldConfig.class);

        RENDER_BIOME_BLEND = Mathf.clamp(0, 7, RENDER_BIOME_BLEND);
        RENDER_SKYLIGHT = Mathf.clamp(0, 15, RENDER_SKYLIGHT);
    }

    @Override
    protected @NonNull Object getClassObject() {
        return this;
    }

    @Override
    protected @Nullable Object getValue(@NonNull String path, @Nullable Object def) {
        if (getConfig().get("world-settings.default." + path) == null) {
            set("world-settings.default." + path, def);
        }
        return get("world-settings." + worldName + "." + path,
                get("world-settings.default." + path, def));
    }

    @Override
    protected void setComment(@NonNull String path, @Nullable String comment) {
        getConfig().setComment("world-settings.default." + path, comment);
    }
}
