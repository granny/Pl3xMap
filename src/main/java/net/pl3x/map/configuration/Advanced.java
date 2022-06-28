package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;

public class Advanced extends AbstractConfig {
    @Key("settings.event-listeners.BlockBreakEvent")
    @Comment("Triggers when a player breaks a block")
    public static boolean BLOCK_BREAK_EVENT = true;

    @Key("settings.event-listeners.BlockPlaceEvent")
    @Comment("Triggers when a player breaks a block")
    public static boolean BLOCK_PLACE_EVENT = true;

    @Key("settings.event-listeners.BlockFadeEvent")
    @Comment("Triggers when a block fades, melts or disappears based on world conditions")
    public static boolean BLOCK_FADE_EVENT = true;

    @Key("settings.event-listeners.BlockBurnEvent")
    @Comment("Triggers when a block is destroyed by fire")
    public static boolean BLOCK_BURN_EVENT = true;

    @Key("settings.event-listeners.BlockExplodeEvent")
    @Comment("Triggers when a block is destroyed by an explosion")
    public static boolean BLOCK_EXPLODE_EVENT = true;

    @Key("settings.event-listeners.BlockFormEvent")
    @Comment("""
            Triggers when a block forms from world conditions
            (snow forming from snowfall, ice forming from cold,
            obsidian/cobblestone forming from contact with water,
            concrete forming from contact with water)""")
    public static boolean BLOCK_FORM_EVENT = true;

    @Key("settings.event-listeners.BlockFromToEvent")
    @Comment("""
            Triggers when a block moves/spreads from block to
            another(water / lava flowing, teleporting dragon eggs)""")
    public static boolean BLOCK_FROM_TO_EVENT = false;

    @Key("settings.event-listeners.BlockGrowEvent")
    @Comment("""
            Triggers when a growable block grows
            (wheat, sugar cane, cactus, watermelon, pumpkin, turtle egg)""")
    public static boolean BLOCK_GROW_EVENT = true;

    @Key("settings.event-listeners.BlockPhysicsEvent")
    @Comment("""
            Triggers when a block physics check is called. This
            event is a high frequency event, it may be called thousands
            of times per a second on a busy server.It is advised to
            enable the event with caution.""")
    public static boolean BLOCK_PHYSICS_EVENT = false;

    @Key("settings.event-listeners.BlockPistonExtendEvent")
    @Comment("Triggers when a piston head extends (pushes)")
    public static boolean BLOCK_PISTON_EXTEND_EVENT = false;

    @Key("settings.event-listeners.BlockPistonRetractEvent")
    @Comment("Triggers when a piston head retracts (pulls)")
    public static boolean BLOCK_PISTON_RETRACT_EVENT = false;

    @Key("settings.event-listeners.BlockSpreadEvent")
    @Comment("""
            Triggers when a block spreads to a new block
            (mushrooms spreading, fire spreading)""")
    public static boolean BLOCK_SPREAD_EVENT = true;

    @Key("settings.event-listeners.ChunkLoadEvent")
    @Comment("Triggers when a chunk loads")
    public static boolean CHUNK_LOAD_EVENT = false;

    @Key("settings.event-listeners.ChunkPopulateEvent")
    @Comment("Triggers when a chunk generates")
    public static boolean CHUNK_POPULATE_EVENT = true;

    @Key("settings.event-listeners.EntityBlockFormEvent")
    @Comment("""
            Triggers when a block forms from entities
            (snowman leaving snow trail, frostwalker enchant forming ice)""")
    public static boolean ENTITY_BLOCK_FORM_EVENT = true;

    @Key("settings.event-listeners.EntityChangeBlockEvent")
    @Comment("""
            Triggers when an entity changes a block and a more
            specific event is not available""")
    public static boolean ENTITY_CHANGE_BLOCK_EVENT = true;

    @Key("settings.event-listeners.EntityExplodeEvent")
    @Comment("Triggers when an entity explodes")
    public static boolean ENTITY_EXPLODE_EVENT = true;

    @Key("settings.event-listeners.FluidLevelChangeEvent")
    @Comment("Triggers when a fluid's level changes")
    public static boolean FLUID_LEVEL_CHANGE_EVENT = true;

    @Key("settings.event-listeners.LeavesDecayEvent")
    @Comment("Triggers when leaves decay naturally")
    public static boolean LEAVES_DECAY_EVENT = true;

    @Key("settings.event-listeners.PlayerJoinEvent")
    @Comment("Triggers when a player joins the server")
    public static boolean PLAYER_JOIN_EVENT = false;

    @Key("settings.event-listeners.PlayerMoveEvent")
    @Comment("Triggers when a player moves or looks around")
    public static boolean PLAYER_MOVE_EVENT = false;

    @Key("settings.event-listeners.PlayerQuitEvent")
    @Comment("""
            Triggers when a player leaves the server
            (quit, kicked, timed out, etc)""")
    public static boolean PLAYER_QUIT_EVENT = false;

    @Key("settings.event-listeners.StructureGrowEvent")
    @Comment("""
            Triggers when an organic structure attempts to
            grow naturally or using bonemeal
            (sapling -> tree, mushroom->huge mushroom)""")
    public static boolean STRUCTURE_GROW_EVENT = true;

    private static final Advanced CONFIG = new Advanced();

    public static void reload() {
        CONFIG.reload(FileUtil.PLUGIN_DIR.resolve("advanced.yml"), Advanced.class);
    }
}
