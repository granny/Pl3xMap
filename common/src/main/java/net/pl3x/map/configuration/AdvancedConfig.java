package net.pl3x.map.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.MemorySection;

public class AdvancedConfig extends AbstractConfig {
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

    @Key("settings.colors.blocks")
    @Comment("""
            Each block has a specific color assigned to it. You can
            pick your own color here for any blocks you want to change.
            Any blocks _not_ in this list will use Mojang's color.
            Setting a color to black (#000000) will make it invisible.""")
    public static Map<Block, Integer> BLOCK_COLORS = new LinkedHashMap<>() {{
        put(Blocks.ACACIA_BUTTON, 0x000000);
        put(Blocks.ACACIA_DOOR, 0xA85F3D);
        put(Blocks.ACACIA_FENCE, 0xA85A32);
        put(Blocks.ACACIA_FENCE_GATE, 0xA85A32);
        put(Blocks.ACACIA_LOG, 0x676157);
        put(Blocks.ACACIA_PLANKS, 0xA85A32);
        put(Blocks.ACACIA_PRESSURE_PLATE, 0xA85A32);
        put(Blocks.ACACIA_SAPLING, 0x777618);
        put(Blocks.ACACIA_SIGN, 0xA85A32);
        put(Blocks.ACACIA_SLAB, 0xA85A32);
        put(Blocks.ACACIA_STAIRS, 0xA85A32);
        put(Blocks.ACACIA_TRAPDOOR, 0x9F5934);
        put(Blocks.ACACIA_WALL_SIGN, 0xA85A32);
        put(Blocks.ACACIA_WOOD, 0x676157);
        put(Blocks.ACTIVATOR_RAIL, 0x725446);
        put(Blocks.ALLIUM, 0xA089B9);
        put(Blocks.AMETHYST_BLOCK, 0x8662BF);
        put(Blocks.AMETHYST_CLUSTER, 0xA47FCF);
        put(Blocks.ANCIENT_DEBRIS, 0x5E4139);
        put(Blocks.ANDESITE, 0x888889);
        put(Blocks.ANDESITE_SLAB, 0x888889);
        put(Blocks.ANDESITE_STAIRS, 0x888889);
        put(Blocks.ANDESITE_WALL, 0x888889);
        put(Blocks.ANVIL, 0x494949);
        put(Blocks.ATTACHED_MELON_STEM, 0x8C8C8C);
        put(Blocks.ATTACHED_PUMPKIN_STEM, 0x8A8A8A);
        put(Blocks.AZALEA, 0x667D30);
        put(Blocks.AZURE_BLUET, 0xACCE82);
        put(Blocks.BAMBOO, 0x5D9013);
        put(Blocks.BAMBOO_SAPLING, 0x5D9013);
        put(Blocks.BARREL, 0x87653B);
        put(Blocks.BARRIER, 0x000000);
        put(Blocks.BASALT, 0x515156);
        put(Blocks.BEACON, 0x74DED8);
        put(Blocks.BEDROCK, 0x565656);
        put(Blocks.BEEHIVE, 0xB4915A);
        put(Blocks.BEETROOTS, 0x007C00);
        put(Blocks.BEE_NEST, 0xCEA44D);
        put(Blocks.BELL, 0xFDEB6E);
        put(Blocks.BIG_DRIPLEAF, 0x729034);
        put(Blocks.BIG_DRIPLEAF_STEM, 0x5C742E);
        put(Blocks.BIRCH_BUTTON, 0x000000);
        put(Blocks.BIRCH_DOOR, 0xE0D6B7);
        put(Blocks.BIRCH_FENCE, 0xC0AF79);
        put(Blocks.BIRCH_FENCE_GATE, 0xC0AF79);
        put(Blocks.BIRCH_LEAVES, 0x668644);
        put(Blocks.BIRCH_LOG, 0xDBDAD5);
        put(Blocks.BIRCH_PLANKS, 0xC0AF79);
        put(Blocks.BIRCH_PRESSURE_PLATE, 0xC0AF79);
        put(Blocks.BIRCH_SAPLING, 0x81A251);
        put(Blocks.BIRCH_SIGN, 0xC0AF79);
        put(Blocks.BIRCH_SLAB, 0xC0AF79);
        put(Blocks.BIRCH_STAIRS, 0xC0AF79);
        put(Blocks.BIRCH_TRAPDOOR, 0xD4C9A6);
        put(Blocks.BIRCH_WALL_SIGN, 0xC0AF79);
        put(Blocks.BIRCH_WOOD, 0xDBDAD5);
        put(Blocks.BLACKSTONE, 0x2A242A);
        put(Blocks.BLACKSTONE_SLAB, 0x2A242A);
        put(Blocks.BLACKSTONE_STAIRS, 0x2A242A);
        put(Blocks.BLACKSTONE_WALL, 0x2A242A);
        put(Blocks.BLACK_BANNER, 0x000000);
        put(Blocks.BLACK_BED, 0x15151A);
        put(Blocks.BLACK_CANDLE, 0x28263C);
        put(Blocks.BLACK_CANDLE_CAKE, 0x28263C);
        put(Blocks.BLACK_CARPET, 0x15151A);
        put(Blocks.BLACK_CONCRETE, 0x080A0F);
        put(Blocks.BLACK_CONCRETE_POWDER, 0x191B20);
        put(Blocks.BLACK_GLAZED_TERRACOTTA, 0x451E20);
        put(Blocks.BLACK_SHULKER_BOX, 0x1A1A1E);
        put(Blocks.BLACK_STAINED_GLASS, 0x191919);
        put(Blocks.BLACK_STAINED_GLASS_PANE, 0x191919);
        put(Blocks.BLACK_TERRACOTTA, 0x251710);
        put(Blocks.BLACK_WALL_BANNER, 0x000000);
        put(Blocks.BLACK_WOOL, 0x15151A);
        put(Blocks.BLAST_FURNACE, 0x4F4E4F);
        put(Blocks.BLUE_BANNER, 0x000000);
        put(Blocks.BLUE_BED, 0x35399D);
        put(Blocks.BLUE_CANDLE, 0x3A4DA2);
        put(Blocks.BLUE_CANDLE_CAKE, 0x3A4DA2);
        put(Blocks.BLUE_CARPET, 0x35399D);
        put(Blocks.BLUE_CONCRETE, 0x2D2F8F);
        put(Blocks.BLUE_CONCRETE_POWDER, 0x4649A7);
        put(Blocks.BLUE_GLAZED_TERRACOTTA, 0x304490);
        put(Blocks.BLUE_ICE, 0x74A8FD);
        put(Blocks.BLUE_ORCHID, 0x30A3AA);
        put(Blocks.BLUE_SHULKER_BOX, 0x2D2F8E);
        put(Blocks.BLUE_STAINED_GLASS, 0x334CB2);
        put(Blocks.BLUE_STAINED_GLASS_PANE, 0x334CB2);
        put(Blocks.BLUE_TERRACOTTA, 0x4A3C5B);
        put(Blocks.BLUE_WALL_BANNER, 0x000000);
        put(Blocks.BLUE_WOOL, 0x35399D);
        put(Blocks.BONE_BLOCK, 0xD0CCB1);
        put(Blocks.BOOKSHELF, 0x735D3A);
        put(Blocks.BRAIN_CORAL, 0xC65598);
        put(Blocks.BRAIN_CORAL_BLOCK, 0xD05CA0);
        put(Blocks.BRAIN_CORAL_FAN, 0xCC559B);
        put(Blocks.BRAIN_CORAL_WALL_FAN, 0xCC559B);
        put(Blocks.BREWING_STAND, 0x7A654F);
        put(Blocks.BRICKS, 0x976153);
        put(Blocks.BRICK_SLAB, 0x976153);
        put(Blocks.BRICK_STAIRS, 0x976153);
        put(Blocks.BRICK_WALL, 0x976153);
        put(Blocks.BROWN_BANNER, 0x000000);
        put(Blocks.BROWN_BED, 0x724829);
        put(Blocks.BROWN_CANDLE, 0x71472A);
        put(Blocks.BROWN_CANDLE_CAKE, 0x71472A);
        put(Blocks.BROWN_CARPET, 0x724829);
        put(Blocks.BROWN_CONCRETE, 0x603C20);
        put(Blocks.BROWN_CONCRETE_POWDER, 0x7E5536);
        put(Blocks.BROWN_GLAZED_TERRACOTTA, 0x7D6A53);
        put(Blocks.BROWN_MUSHROOM, 0x9A755C);
        put(Blocks.BROWN_MUSHROOM_BLOCK, 0x957051);
        put(Blocks.BROWN_SHULKER_BOX, 0x6C4325);
        put(Blocks.BROWN_STAINED_GLASS, 0x664C33);
        put(Blocks.BROWN_STAINED_GLASS_PANE, 0x664C33);
        put(Blocks.BROWN_TERRACOTTA, 0x4D3324);
        put(Blocks.BROWN_WALL_BANNER, 0x000000);
        put(Blocks.BROWN_WOOL, 0x724829);
        put(Blocks.BUBBLE_COLUMN, 0x4040FF);
        put(Blocks.BUBBLE_CORAL, 0xA0179E);
        put(Blocks.BUBBLE_CORAL_BLOCK, 0xA61BA3);
        put(Blocks.BUBBLE_CORAL_FAN, 0xA121A0);
        put(Blocks.BUBBLE_CORAL_WALL_FAN, 0xA121A0);
        put(Blocks.BUDDING_AMETHYST, 0x8560BA);
        put(Blocks.CACTUS, 0x58822D);
        put(Blocks.CAKE, 0xF7DBD4);
        put(Blocks.CALCITE, 0xE0E1DD);
        put(Blocks.CAMPFIRE, 0xDCA03C);
        put(Blocks.CANDLE, 0xE9CB9B);
        put(Blocks.CANDLE_CAKE, 0xF7DBD4);
        put(Blocks.CARROTS, 0x367A28);
        put(Blocks.CARTOGRAPHY_TABLE, 0x6D5D47);
        put(Blocks.CARVED_PUMPKIN, 0x915111);
        put(Blocks.CAULDRON, 0x4A494A);
        put(Blocks.CAVE_AIR, 0x000000);
        put(Blocks.CAVE_VINES, 0x5A6D29);
        put(Blocks.CAVE_VINES_PLANT, 0x596626);
        put(Blocks.CHAIN, 0x333A4A);
        put(Blocks.CHAIN_COMMAND_BLOCK, 0x86A497);
        put(Blocks.CHEST, 0x866025);
        put(Blocks.CHIPPED_ANVIL, 0x494949);
        put(Blocks.CHISELED_DEEPSLATE, 0x373738);
        put(Blocks.CHISELED_NETHER_BRICKS, 0x30181C);
        put(Blocks.CHISELED_POLISHED_BLACKSTONE, 0x363139);
        put(Blocks.CHISELED_QUARTZ_BLOCK, 0xE8E3DA);
        put(Blocks.CHISELED_RED_SANDSTONE, 0xB7601B);
        put(Blocks.CHISELED_SANDSTONE, 0xD8CB9B);
        put(Blocks.CHISELED_STONE_BRICKS, 0x787778);
        put(Blocks.CHORUS_FLOWER, 0xA184A1);
        put(Blocks.CHORUS_PLANT, 0x5D395D);
        put(Blocks.CLAY, 0xA1A6B3);
        put(Blocks.COAL_BLOCK, 0x101010);
        put(Blocks.COAL_ORE, 0x686867);
        put(Blocks.COARSE_DIRT, 0x77553B);
        put(Blocks.COBBLED_DEEPSLATE, 0x4D4D50);
        put(Blocks.COBBLED_DEEPSLATE_SLAB, 0x4D4D50);
        put(Blocks.COBBLED_DEEPSLATE_STAIRS, 0x4D4D50);
        put(Blocks.COBBLED_DEEPSLATE_WALL, 0x4D4D50);
        put(Blocks.COBBLESTONE, 0x807F7F);
        put(Blocks.COBBLESTONE_SLAB, 0x807F7F);
        put(Blocks.COBBLESTONE_STAIRS, 0x807F7F);
        put(Blocks.COBBLESTONE_WALL, 0x807F7F);
        put(Blocks.COBWEB, 0xE6EAEB);
        put(Blocks.COMMAND_BLOCK, 0xAD846E);
        put(Blocks.COMPARATOR, 0xA9A3A0);
        put(Blocks.COMPOSTER, 0x976233);
        put(Blocks.CONDUIT, 0x9F8B71);
        put(Blocks.COPPER_BLOCK, 0xC06C50);
        put(Blocks.COPPER_ORE, 0x7D7D77);
        put(Blocks.CORNFLOWER, 0x507896);
        put(Blocks.CRACKED_DEEPSLATE_BRICKS, 0x414041);
        put(Blocks.CRACKED_DEEPSLATE_TILES, 0x353535);
        put(Blocks.CRACKED_NETHER_BRICKS, 0x281418);
        put(Blocks.CRACKED_POLISHED_BLACKSTONE_BRICKS, 0x2C262C);
        put(Blocks.CRACKED_STONE_BRICKS, 0x767676);
        put(Blocks.CRAFTING_TABLE, 0x7B4B2B);
        put(Blocks.CREEPER_HEAD, 0x74AE6E);
        put(Blocks.CREEPER_WALL_HEAD, 0x74AE6E);
        put(Blocks.CRIMSON_BUTTON, 0x000000);
        put(Blocks.CRIMSON_DOOR, 0x72374F);
        put(Blocks.CRIMSON_FENCE, 0x653147);
        put(Blocks.CRIMSON_FENCE_GATE, 0x653147);
        put(Blocks.CRIMSON_FUNGUS, 0x8F2C1D);
        put(Blocks.CRIMSON_HYPHAE, 0x5D1A1E);
        put(Blocks.CRIMSON_NYLIUM, 0x832020);
        put(Blocks.CRIMSON_PLANKS, 0x653147);
        put(Blocks.CRIMSON_PRESSURE_PLATE, 0x653147);
        put(Blocks.CRIMSON_ROOTS, 0x7F082A);
        put(Blocks.CRIMSON_SIGN, 0x653147);
        put(Blocks.CRIMSON_SLAB, 0x653147);
        put(Blocks.CRIMSON_STAIRS, 0x653147);
        put(Blocks.CRIMSON_STEM, 0x5D1A1E);
        put(Blocks.CRIMSON_TRAPDOOR, 0x693349);
        put(Blocks.CRIMSON_WALL_SIGN, 0x653147);
        put(Blocks.CRYING_OBSIDIAN, 0x220A3F);
        put(Blocks.CUT_COPPER, 0xBF6B51);
        put(Blocks.CUT_COPPER_SLAB, 0xBF6B51);
        put(Blocks.CUT_COPPER_STAIRS, 0xBF6B51);
        put(Blocks.CUT_RED_SANDSTONE, 0xBE6620);
        put(Blocks.CUT_RED_SANDSTONE_SLAB, 0xBE6620);
        put(Blocks.CUT_SANDSTONE, 0xDACFA0);
        put(Blocks.CUT_SANDSTONE_SLAB, 0xDACFA0);
        put(Blocks.CYAN_BANNER, 0x000000);
        put(Blocks.CYAN_BED, 0x158A91);
        put(Blocks.CYAN_CANDLE, 0x117E7E);
        put(Blocks.CYAN_CANDLE_CAKE, 0x117E7E);
        put(Blocks.CYAN_CARPET, 0x158A91);
        put(Blocks.CYAN_CONCRETE, 0x157788);
        put(Blocks.CYAN_CONCRETE_POWDER, 0x25949D);
        put(Blocks.CYAN_GLAZED_TERRACOTTA, 0x34747B);
        put(Blocks.CYAN_SHULKER_BOX, 0x157B89);
        put(Blocks.CYAN_STAINED_GLASS, 0x4C7F99);
        put(Blocks.CYAN_STAINED_GLASS_PANE, 0x4C7F99);
        put(Blocks.CYAN_TERRACOTTA, 0x575B5B);
        put(Blocks.CYAN_WALL_BANNER, 0x000000);
        put(Blocks.CYAN_WOOL, 0x158A91);
        put(Blocks.DAMAGED_ANVIL, 0x494949);
        put(Blocks.DANDELION, 0x9EB02E);
        put(Blocks.DARK_OAK_BUTTON, 0x000000);
        put(Blocks.DARK_OAK_DOOR, 0x4C3319);
        put(Blocks.DARK_OAK_FENCE, 0x432B14);
        put(Blocks.DARK_OAK_FENCE_GATE, 0x432B14);
        put(Blocks.DARK_OAK_LOG, 0x3C2F1A);
        put(Blocks.DARK_OAK_PLANKS, 0x432B14);
        put(Blocks.DARK_OAK_PRESSURE_PLATE, 0x432B14);
        put(Blocks.DARK_OAK_SAPLING, 0x3C5B1E);
        put(Blocks.DARK_OAK_SIGN, 0x432B14);
        put(Blocks.DARK_OAK_SLAB, 0x432B14);
        put(Blocks.DARK_OAK_STAIRS, 0x432B14);
        put(Blocks.DARK_OAK_TRAPDOOR, 0x4B3217);
        put(Blocks.DARK_OAK_WALL_SIGN, 0x432B14);
        put(Blocks.DARK_OAK_WOOD, 0x3C2F1A);
        put(Blocks.DARK_PRISMARINE, 0x345C4C);
        put(Blocks.DARK_PRISMARINE_SLAB, 0x345C4C);
        put(Blocks.DARK_PRISMARINE_STAIRS, 0x345C4C);
        put(Blocks.DAYLIGHT_DETECTOR, 0x867862);
        put(Blocks.DEAD_BRAIN_CORAL, 0x867D79);
        put(Blocks.DEAD_BRAIN_CORAL_BLOCK, 0x7D7673);
        put(Blocks.DEAD_BRAIN_CORAL_FAN, 0x867E7A);
        put(Blocks.DEAD_BRAIN_CORAL_WALL_FAN, 0x867E7A);
        put(Blocks.DEAD_BUBBLE_CORAL, 0x847C78);
        put(Blocks.DEAD_BUBBLE_CORAL_BLOCK, 0x847C78);
        put(Blocks.DEAD_BUBBLE_CORAL_FAN, 0x8D8783);
        put(Blocks.DEAD_BUBBLE_CORAL_WALL_FAN, 0x8D8783);
        put(Blocks.DEAD_BUSH, 0x6D5029);
        put(Blocks.DEAD_FIRE_CORAL, 0x89807C);
        put(Blocks.DEAD_FIRE_CORAL_BLOCK, 0x847C78);
        put(Blocks.DEAD_FIRE_CORAL_FAN, 0x7D7673);
        put(Blocks.DEAD_FIRE_CORAL_WALL_FAN, 0x7D7673);
        put(Blocks.DEAD_HORN_CORAL, 0x8F8782);
        put(Blocks.DEAD_HORN_CORAL_BLOCK, 0x857E7A);
        put(Blocks.DEAD_HORN_CORAL_FAN, 0x877F7A);
        put(Blocks.DEAD_HORN_CORAL_WALL_FAN, 0x877F7A);
        put(Blocks.DEAD_TUBE_CORAL, 0x77706C);
        put(Blocks.DEAD_TUBE_CORAL_BLOCK, 0x837C78);
        put(Blocks.DEAD_TUBE_CORAL_FAN, 0x807976);
        put(Blocks.DEAD_TUBE_CORAL_WALL_FAN, 0x807976);
        put(Blocks.DEEPSLATE, 0x575759);
        put(Blocks.DEEPSLATE_BRICKS, 0x474747);
        put(Blocks.DEEPSLATE_BRICK_SLAB, 0x474747);
        put(Blocks.DEEPSLATE_BRICK_STAIRS, 0x474747);
        put(Blocks.DEEPSLATE_BRICK_WALL, 0x474747);
        put(Blocks.DEEPSLATE_COAL_ORE, 0x49494B);
        put(Blocks.DEEPSLATE_COPPER_ORE, 0x5D5E59);
        put(Blocks.DEEPSLATE_DIAMOND_ORE, 0x536D6E);
        put(Blocks.DEEPSLATE_EMERALD_ORE, 0x4D6A57);
        put(Blocks.DEEPSLATE_GOLD_ORE, 0x76684D);
        put(Blocks.DEEPSLATE_IRON_ORE, 0x6D6560);
        put(Blocks.DEEPSLATE_LAPIS_ORE, 0x4F5B76);
        put(Blocks.DEEPSLATE_REDSTONE_ORE, 0x6B4849);
        put(Blocks.DEEPSLATE_TILES, 0x373738);
        put(Blocks.DEEPSLATE_TILE_SLAB, 0x373738);
        put(Blocks.DEEPSLATE_TILE_STAIRS, 0x373738);
        put(Blocks.DEEPSLATE_TILE_WALL, 0x373738);
        put(Blocks.DETECTOR_RAIL, 0x7A6658);
        put(Blocks.DIAMOND_BLOCK, 0x65EFE5);
        put(Blocks.DIAMOND_ORE, 0x788F8F);
        put(Blocks.DIORITE, 0xBDBDBD);
        put(Blocks.DIORITE_SLAB, 0xBDBDBD);
        put(Blocks.DIORITE_STAIRS, 0xBDBDBD);
        put(Blocks.DIORITE_WALL, 0xBDBDBD);
        put(Blocks.DIRT, 0x866043);
        put(Blocks.DIRT_PATH, 0x947A41);
        put(Blocks.DISPENSER, 0x727171);
        put(Blocks.DRAGON_EGG, 0x0D0910);
        put(Blocks.DRAGON_HEAD, 0x1D1A1E);
        put(Blocks.DRAGON_WALL_HEAD, 0x1D1A1E);
        put(Blocks.DRIED_KELP_BLOCK, 0x343C28);
        put(Blocks.DRIPSTONE_BLOCK, 0x866B5C);
        put(Blocks.DROPPER, 0x727171);
        put(Blocks.EMERALD_BLOCK, 0x2BCD5A);
        put(Blocks.EMERALD_ORE, 0x6A8972);
        put(Blocks.ENCHANTING_TABLE, 0x7E3F4B);
        put(Blocks.ENDER_CHEST, 0x2C3D3F);
        put(Blocks.END_GATEWAY, 0x030303);
        put(Blocks.END_PORTAL, 0x030303);
        put(Blocks.END_PORTAL_FRAME, 0x5A755E);
        put(Blocks.END_ROD, 0xC8BFB5);
        put(Blocks.END_STONE, 0xDBDF9E);
        put(Blocks.END_STONE_BRICKS, 0xDBE0A2);
        put(Blocks.END_STONE_BRICK_SLAB, 0xDBE0A2);
        put(Blocks.END_STONE_BRICK_STAIRS, 0xDBE0A2);
        put(Blocks.END_STONE_BRICK_WALL, 0xDBE0A2);
        put(Blocks.EXPOSED_COPPER, 0xA17E68);
        put(Blocks.EXPOSED_CUT_COPPER, 0x9B7A65);
        put(Blocks.EXPOSED_CUT_COPPER_SLAB, 0x9B7A65);
        put(Blocks.EXPOSED_CUT_COPPER_STAIRS, 0x9B7A65);
        put(Blocks.FERN, 0x000000);
        put(Blocks.FIRE, 0xD48C35);
        put(Blocks.FIRE_CORAL, 0xA6252F);
        put(Blocks.FIRE_CORAL_BLOCK, 0xA4232F);
        put(Blocks.FIRE_CORAL_FAN, 0x9F232E);
        put(Blocks.FIRE_CORAL_WALL_FAN, 0x9F232E);
        put(Blocks.FLETCHING_TABLE, 0xC6B687);
        put(Blocks.FLOWERING_AZALEA, 0x717A41);
        put(Blocks.FLOWERING_AZALEA_LEAVES, 0x9D5CAB);
        put(Blocks.FLOWER_POT, 0x000000);
        put(Blocks.FROGSPAWN, 0x6A5B52);
        put(Blocks.FROSTED_ICE, 0x8CB5FD);
        put(Blocks.FURNACE, 0x727171);
        put(Blocks.GILDED_BLACKSTONE, 0x382B27);
        put(Blocks.GLASS, 0xB0D6DB);
        put(Blocks.GLASS_PANE, 0xB0D6DB);
        put(Blocks.GLOWSTONE, 0xAD8455);
        put(Blocks.GLOW_LICHEN, 0x70837A);
        put(Blocks.GOLD_BLOCK, 0xF8D33E);
        put(Blocks.GOLD_ORE, 0x938769);
        put(Blocks.GRANITE, 0x956756);
        put(Blocks.GRANITE_SLAB, 0x956756);
        put(Blocks.GRANITE_STAIRS, 0x956756);
        put(Blocks.GRANITE_WALL, 0x956756);
        put(Blocks.GRASS, 0x000000);
        put(Blocks.GRAVEL, 0x84807F);
        put(Blocks.GRAY_BANNER, 0x000000);
        put(Blocks.GRAY_BED, 0x3F4448);
        put(Blocks.GRAY_CANDLE, 0x515F62);
        put(Blocks.GRAY_CANDLE_CAKE, 0x515F62);
        put(Blocks.GRAY_CARPET, 0x3F4448);
        put(Blocks.GRAY_CONCRETE, 0x373A3E);
        put(Blocks.GRAY_CONCRETE_POWDER, 0x4D5155);
        put(Blocks.GRAY_GLAZED_TERRACOTTA, 0x535B5E);
        put(Blocks.GRAY_SHULKER_BOX, 0x383C40);
        put(Blocks.GRAY_STAINED_GLASS, 0x4C4C4C);
        put(Blocks.GRAY_STAINED_GLASS_PANE, 0x4C4C4C);
        put(Blocks.GRAY_TERRACOTTA, 0x3A2A24);
        put(Blocks.GRAY_WALL_BANNER, 0x000000);
        put(Blocks.GRAY_WOOL, 0x3F4448);
        put(Blocks.GREEN_BANNER, 0x000000);
        put(Blocks.GREEN_BED, 0x556E1B);
        put(Blocks.GREEN_CANDLE, 0x4A6215);
        put(Blocks.GREEN_CANDLE_CAKE, 0x4A6215);
        put(Blocks.GREEN_CARPET, 0x556E1B);
        put(Blocks.GREEN_CONCRETE, 0x495B24);
        put(Blocks.GREEN_CONCRETE_POWDER, 0x61772D);
        put(Blocks.GREEN_GLAZED_TERRACOTTA, 0x728B3F);
        put(Blocks.GREEN_SHULKER_BOX, 0x50661F);
        put(Blocks.GREEN_STAINED_GLASS, 0x667F33);
        put(Blocks.GREEN_STAINED_GLASS_PANE, 0x667F33);
        put(Blocks.GREEN_TERRACOTTA, 0x4C532A);
        put(Blocks.GREEN_WALL_BANNER, 0x000000);
        put(Blocks.GREEN_WOOL, 0x556E1B);
        put(Blocks.GRINDSTONE, 0x8A8A8A);
        put(Blocks.HANGING_ROOTS, 0xA3755E);
        put(Blocks.HAY_BLOCK, 0xA68C0C);
        put(Blocks.HEAVY_WEIGHTED_PRESSURE_PLATE, 0xDEDEDE);
        put(Blocks.HONEYCOMB_BLOCK, 0xE5951E);
        put(Blocks.HONEY_BLOCK, 0xFBB934);
        put(Blocks.HOPPER, 0x4B4A4B);
        put(Blocks.HORN_CORAL, 0xD1BA3F);
        put(Blocks.HORN_CORAL_BLOCK, 0xD8C742);
        put(Blocks.HORN_CORAL_FAN, 0xCFB83D);
        put(Blocks.HORN_CORAL_WALL_FAN, 0xCFB83D);
        put(Blocks.ICE, 0x91B8FE);
        put(Blocks.INFESTED_CHISELED_STONE_BRICKS, 0x787778);
        put(Blocks.INFESTED_COBBLESTONE, 0x807F7F);
        put(Blocks.INFESTED_CRACKED_STONE_BRICKS, 0x767676);
        put(Blocks.INFESTED_DEEPSLATE, 0x575759);
        put(Blocks.INFESTED_MOSSY_STONE_BRICKS, 0x74796A);
        put(Blocks.INFESTED_STONE, 0x7E7E7E);
        put(Blocks.INFESTED_STONE_BRICKS, 0x7A7A7A);
        put(Blocks.IRON_BARS, 0x898C88);
        put(Blocks.IRON_BLOCK, 0xDEDEDE);
        put(Blocks.IRON_DOOR, 0xC3C2C2);
        put(Blocks.IRON_ORE, 0x8A827B);
        put(Blocks.IRON_TRAPDOOR, 0xCCCCCC);
        put(Blocks.JACK_O_LANTERN, 0xDB9F3A);
        put(Blocks.JIGSAW, 0x5A4E5B);
        put(Blocks.JUKEBOX, 0x604130);
        put(Blocks.JUNGLE_BUTTON, 0x000000);
        put(Blocks.JUNGLE_DOOR, 0xA47854);
        put(Blocks.JUNGLE_FENCE, 0xA17351);
        put(Blocks.JUNGLE_FENCE_GATE, 0xA17351);
        put(Blocks.JUNGLE_LOG, 0x554419);
        put(Blocks.JUNGLE_PLANKS, 0xA17351);
        put(Blocks.JUNGLE_PRESSURE_PLATE, 0xA17351);
        put(Blocks.JUNGLE_SAPLING, 0x305111);
        put(Blocks.JUNGLE_SIGN, 0xA17351);
        put(Blocks.JUNGLE_SLAB, 0xA17351);
        put(Blocks.JUNGLE_STAIRS, 0xA17351);
        put(Blocks.JUNGLE_TRAPDOOR, 0x9D7150);
        put(Blocks.JUNGLE_WALL_SIGN, 0xA17351);
        put(Blocks.JUNGLE_WOOD, 0x554419);
        put(Blocks.KELP, 0x578B2C);
        put(Blocks.KELP_PLANT, 0x57802A);
        put(Blocks.LADDER, 0x000000);
        put(Blocks.LANTERN, 0x6A5B54);
        put(Blocks.LAPIS_BLOCK, 0x1F438C);
        put(Blocks.LAPIS_ORE, 0x68758F);
        put(Blocks.LARGE_AMETHYST_BUD, 0xA582CD);
        put(Blocks.LARGE_FERN, 0x000000);
        put(Blocks.LAVA, 0xD45A12);
        put(Blocks.LAVA_CAULDRON, 0x4A494A);
        put(Blocks.LECTERN, 0xAE8953);
        put(Blocks.LEVER, 0x000000);
        put(Blocks.LIGHT, 0x000000);
        put(Blocks.LIGHTNING_ROD, 0xC46F53);
        put(Blocks.LIGHT_BLUE_BANNER, 0x000000);
        put(Blocks.LIGHT_BLUE_BED, 0x3AAFD9);
        put(Blocks.LIGHT_BLUE_CANDLE, 0x238BC5);
        put(Blocks.LIGHT_BLUE_CANDLE_CAKE, 0x238BC5);
        put(Blocks.LIGHT_BLUE_CARPET, 0x3AAFD9);
        put(Blocks.LIGHT_BLUE_CONCRETE, 0x2489C7);
        put(Blocks.LIGHT_BLUE_CONCRETE_POWDER, 0x4AB5D5);
        put(Blocks.LIGHT_BLUE_GLAZED_TERRACOTTA, 0x60A6D1);
        put(Blocks.LIGHT_BLUE_SHULKER_BOX, 0x33A6D5);
        put(Blocks.LIGHT_BLUE_STAINED_GLASS, 0x6699D8);
        put(Blocks.LIGHT_BLUE_STAINED_GLASS_PANE, 0x6699D8);
        put(Blocks.LIGHT_BLUE_TERRACOTTA, 0x716D8A);
        put(Blocks.LIGHT_BLUE_WALL_BANNER, 0x000000);
        put(Blocks.LIGHT_BLUE_WOOL, 0x3AAFD9);
        put(Blocks.LIGHT_GRAY_BANNER, 0x000000);
        put(Blocks.LIGHT_GRAY_BED, 0x8E8E87);
        put(Blocks.LIGHT_GRAY_CANDLE, 0x787A72);
        put(Blocks.LIGHT_GRAY_CANDLE_CAKE, 0x787A72);
        put(Blocks.LIGHT_GRAY_CARPET, 0x8E8E87);
        put(Blocks.LIGHT_GRAY_CONCRETE, 0x7D7D73);
        put(Blocks.LIGHT_GRAY_CONCRETE_POWDER, 0x9B9B94);
        put(Blocks.LIGHT_GRAY_GLAZED_TERRACOTTA, 0x91A7A9);
        put(Blocks.LIGHT_GRAY_SHULKER_BOX, 0x7F7F76);
        put(Blocks.LIGHT_GRAY_STAINED_GLASS, 0x999999);
        put(Blocks.LIGHT_GRAY_STAINED_GLASS_PANE, 0x999999);
        put(Blocks.LIGHT_GRAY_TERRACOTTA, 0x876B62);
        put(Blocks.LIGHT_GRAY_WALL_BANNER, 0x000000);
        put(Blocks.LIGHT_GRAY_WOOL, 0x8E8E87);
        put(Blocks.LIGHT_WEIGHTED_PRESSURE_PLATE, 0xF8D33E);
        put(Blocks.LILAC, 0x977D8F);
        put(Blocks.LILY_OF_THE_VALLEY, 0x7DB061);
        put(Blocks.LILY_PAD, 0x208030);
        put(Blocks.LIME_BANNER, 0x000000);
        put(Blocks.LIME_BED, 0x70B91A);
        put(Blocks.LIME_CANDLE, 0x63AD17);
        put(Blocks.LIME_CANDLE_CAKE, 0x63AD17);
        put(Blocks.LIME_CARPET, 0x70B91A);
        put(Blocks.LIME_CONCRETE, 0x5EA919);
        put(Blocks.LIME_CONCRETE_POWDER, 0x7EBD2A);
        put(Blocks.LIME_GLAZED_TERRACOTTA, 0xA3C636);
        put(Blocks.LIME_SHULKER_BOX, 0x66AF17);
        put(Blocks.LIME_STAINED_GLASS, 0x7FCC19);
        put(Blocks.LIME_STAINED_GLASS_PANE, 0x7FCC19);
        put(Blocks.LIME_TERRACOTTA, 0x687635);
        put(Blocks.LIME_WALL_BANNER, 0x000000);
        put(Blocks.LIME_WOOL, 0x70B91A);
        put(Blocks.LODESTONE, 0x929397);
        put(Blocks.LOOM, 0x8D775D);
        put(Blocks.MAGENTA_BANNER, 0x000000);
        put(Blocks.MAGENTA_BED, 0xBE45B4);
        put(Blocks.MAGENTA_CANDLE, 0xA22F9A);
        put(Blocks.MAGENTA_CANDLE_CAKE, 0xA22F9A);
        put(Blocks.MAGENTA_CARPET, 0xBE45B4);
        put(Blocks.MAGENTA_CONCRETE, 0xA9309F);
        put(Blocks.MAGENTA_CONCRETE_POWDER, 0xC154B9);
        put(Blocks.MAGENTA_GLAZED_TERRACOTTA, 0xCF63BE);
        put(Blocks.MAGENTA_SHULKER_BOX, 0xB037A5);
        put(Blocks.MAGENTA_STAINED_GLASS, 0xB24CD8);
        put(Blocks.MAGENTA_STAINED_GLASS_PANE, 0xB24CD8);
        put(Blocks.MAGENTA_TERRACOTTA, 0x96586D);
        put(Blocks.MAGENTA_WALL_BANNER, 0x000000);
        put(Blocks.MAGENTA_WOOL, 0xBE45B4);
        put(Blocks.MAGMA_BLOCK, 0x8E3F20);
        put(Blocks.MANGROVE_BUTTON, 0x000000);
        put(Blocks.MANGROVE_DOOR, 0x70302E);
        put(Blocks.MANGROVE_FENCE, 0x763631);
        put(Blocks.MANGROVE_FENCE_GATE, 0x763631);
        put(Blocks.MANGROVE_LOG, 0x544329);
        put(Blocks.MANGROVE_PLANKS, 0x763631);
        put(Blocks.MANGROVE_PRESSURE_PLATE, 0x763631);
        put(Blocks.MANGROVE_PROPAGULE, 0x5FAF54);
        put(Blocks.MANGROVE_ROOTS, 0x4B3C27);
        put(Blocks.MANGROVE_SIGN, 0x763631);
        put(Blocks.MANGROVE_SLAB, 0x763631);
        put(Blocks.MANGROVE_STAIRS, 0x763631);
        put(Blocks.MANGROVE_TRAPDOOR, 0x6F2F2B);
        put(Blocks.MANGROVE_WALL_SIGN, 0x763631);
        put(Blocks.MANGROVE_WOOD, 0x544329);
        put(Blocks.MEDIUM_AMETHYST_BUD, 0xA37CCC);
        put(Blocks.MELON, 0x6D901E);
        put(Blocks.MELON_STEM, 0x9B9B9B);
        put(Blocks.MOSSY_COBBLESTONE, 0x6D765E);
        put(Blocks.MOSSY_COBBLESTONE_SLAB, 0x6D765E);
        put(Blocks.MOSSY_COBBLESTONE_STAIRS, 0x6D765E);
        put(Blocks.MOSSY_COBBLESTONE_WALL, 0x6D765E);
        put(Blocks.MOSSY_STONE_BRICKS, 0x74796A);
        put(Blocks.MOSSY_STONE_BRICK_SLAB, 0x74796A);
        put(Blocks.MOSSY_STONE_BRICK_STAIRS, 0x74796A);
        put(Blocks.MOSSY_STONE_BRICK_WALL, 0x74796A);
        put(Blocks.MOSS_BLOCK, 0x596E2D);
        put(Blocks.MOSS_CARPET, 0x596E2D);
        put(Blocks.MOVING_PISTON, 0x6E6961);
        put(Blocks.MUD, 0x3C3A3D);
        put(Blocks.MUDDY_MANGROVE_ROOTS, 0x463B2D);
        put(Blocks.MUD_BRICKS, 0x89684F);
        put(Blocks.MUD_BRICK_SLAB, 0x89684F);
        put(Blocks.MUD_BRICK_STAIRS, 0x89684F);
        put(Blocks.MUD_BRICK_WALL, 0x89684F);
        put(Blocks.MUSHROOM_STEM, 0xCBC4B9);
        put(Blocks.MYCELIUM, 0x6F6365);
        put(Blocks.NETHERITE_BLOCK, 0x443F41);
        put(Blocks.NETHERRACK, 0x622727);
        put(Blocks.NETHER_BRICKS, 0x2C161A);
        put(Blocks.NETHER_BRICK_FENCE, 0x2C161A);
        put(Blocks.NETHER_BRICK_SLAB, 0x2C161A);
        put(Blocks.NETHER_BRICK_STAIRS, 0x2C161A);
        put(Blocks.NETHER_BRICK_WALL, 0x2C161A);
        put(Blocks.NETHER_GOLD_ORE, 0x76392B);
        put(Blocks.NETHER_PORTAL, 0x590CC1);
        put(Blocks.NETHER_QUARTZ_ORE, 0x794642);
        put(Blocks.NETHER_SPROUTS, 0x139985);
        put(Blocks.NETHER_WART, 0x711314);
        put(Blocks.NETHER_WART_BLOCK, 0x730302);
        put(Blocks.NOTE_BLOCK, 0x5C3C29);
        put(Blocks.OAK_BUTTON, 0x000000);
        put(Blocks.OAK_DOOR, 0x8C6E41);
        put(Blocks.OAK_FENCE, 0xA2834F);
        put(Blocks.OAK_FENCE_GATE, 0xA2834F);
        put(Blocks.OAK_LOG, 0x6D5533);
        put(Blocks.OAK_PLANKS, 0xA2834F);
        put(Blocks.OAK_PRESSURE_PLATE, 0xA2834F);
        put(Blocks.OAK_SAPLING, 0x4E6A29);
        put(Blocks.OAK_SIGN, 0xA2834F);
        put(Blocks.OAK_SLAB, 0xA2834F);
        put(Blocks.OAK_STAIRS, 0xA2834F);
        put(Blocks.OAK_TRAPDOOR, 0x80663B);
        put(Blocks.OAK_WALL_SIGN, 0xA2834F);
        put(Blocks.OAK_WOOD, 0x6D5533);
        put(Blocks.OBSERVER, 0x676767);
        put(Blocks.OBSIDIAN, 0x0F0B19);
        put(Blocks.OCHRE_FROGLIGHT, 0xFCF8D5);
        put(Blocks.ORANGE_BANNER, 0x000000);
        put(Blocks.ORANGE_BED, 0xF17613);
        put(Blocks.ORANGE_CANDLE, 0xDD660A);
        put(Blocks.ORANGE_CANDLE_CAKE, 0xDD660A);
        put(Blocks.ORANGE_CARPET, 0xF17613);
        put(Blocks.ORANGE_CONCRETE, 0xE06101);
        put(Blocks.ORANGE_CONCRETE_POWDER, 0xE38420);
        put(Blocks.ORANGE_GLAZED_TERRACOTTA, 0xA29258);
        put(Blocks.ORANGE_SHULKER_BOX, 0xEC6C0A);
        put(Blocks.ORANGE_STAINED_GLASS, 0xD87F33);
        put(Blocks.ORANGE_STAINED_GLASS_PANE, 0xD87F33);
        put(Blocks.ORANGE_TERRACOTTA, 0xA25426);
        put(Blocks.ORANGE_TULIP, 0x5E8E1F);
        put(Blocks.ORANGE_WALL_BANNER, 0x000000);
        put(Blocks.ORANGE_WOOL, 0xF17613);
        put(Blocks.OXEYE_DAISY, 0xB7CC91);
        put(Blocks.OXIDIZED_COPPER, 0x53A486);
        put(Blocks.OXIDIZED_CUT_COPPER, 0x509A7F);
        put(Blocks.OXIDIZED_CUT_COPPER_SLAB, 0x509A7F);
        put(Blocks.OXIDIZED_CUT_COPPER_STAIRS, 0x509A7F);
        put(Blocks.PACKED_ICE, 0x8DB4FA);
        put(Blocks.PACKED_MUD, 0x8F6B50);
        put(Blocks.PEARLESCENT_FROGLIGHT, 0xF8F3F3);
        put(Blocks.PEONY, 0x817E8A);
        put(Blocks.PETRIFIED_OAK_SLAB, 0x6D5533);
        put(Blocks.PINK_BANNER, 0x000000);
        put(Blocks.PINK_BED, 0xEE8DAC);
        put(Blocks.PINK_CANDLE, 0xD26890);
        put(Blocks.PINK_CANDLE_CAKE, 0xD26890);
        put(Blocks.PINK_CARPET, 0xEE8DAC);
        put(Blocks.PINK_CONCRETE, 0xD6658F);
        put(Blocks.PINK_CONCRETE_POWDER, 0xE59AB5);
        put(Blocks.PINK_GLAZED_TERRACOTTA, 0xED9BB6);
        put(Blocks.PINK_SHULKER_BOX, 0xE87C9F);
        put(Blocks.PINK_STAINED_GLASS, 0xF27FA5);
        put(Blocks.PINK_STAINED_GLASS_PANE, 0xF27FA5);
        put(Blocks.PINK_TERRACOTTA, 0xA24E4F);
        put(Blocks.PINK_TULIP, 0x649E50);
        put(Blocks.PINK_WALL_BANNER, 0x000000);
        put(Blocks.PINK_WOOL, 0xEE8DAC);
        put(Blocks.PISTON, 0x6E6961);
        put(Blocks.PISTON_HEAD, 0x987E52);
        put(Blocks.PLAYER_HEAD, 0x2A1C0C);
        put(Blocks.PLAYER_WALL_HEAD, 0x2A1C0C);
        put(Blocks.PODZOL, 0x5C3F18);
        put(Blocks.POINTED_DRIPSTONE, 0x816659);
        put(Blocks.POLISHED_ANDESITE, 0x848786);
        put(Blocks.POLISHED_ANDESITE_SLAB, 0x848786);
        put(Blocks.POLISHED_ANDESITE_STAIRS, 0x848786);
        put(Blocks.POLISHED_BASALT, 0x656466);
        put(Blocks.POLISHED_BLACKSTONE, 0x353139);
        put(Blocks.POLISHED_BLACKSTONE_BRICKS, 0x302B32);
        put(Blocks.POLISHED_BLACKSTONE_BRICK_SLAB, 0x302B32);
        put(Blocks.POLISHED_BLACKSTONE_BRICK_STAIRS, 0x302B32);
        put(Blocks.POLISHED_BLACKSTONE_BRICK_WALL, 0x302B32);
        put(Blocks.POLISHED_BLACKSTONE_BUTTON, 0x000000);
        put(Blocks.POLISHED_BLACKSTONE_PRESSURE_PLATE, 0x353139);
        put(Blocks.POLISHED_BLACKSTONE_SLAB, 0x353139);
        put(Blocks.POLISHED_BLACKSTONE_STAIRS, 0x353139);
        put(Blocks.POLISHED_BLACKSTONE_WALL, 0x353139);
        put(Blocks.POLISHED_DEEPSLATE, 0x484849);
        put(Blocks.POLISHED_DEEPSLATE_SLAB, 0x484849);
        put(Blocks.POLISHED_DEEPSLATE_STAIRS, 0x484849);
        put(Blocks.POLISHED_DEEPSLATE_WALL, 0x484849);
        put(Blocks.POLISHED_DIORITE, 0xC3C3C5);
        put(Blocks.POLISHED_DIORITE_SLAB, 0xC3C3C5);
        put(Blocks.POLISHED_DIORITE_STAIRS, 0xC3C3C5);
        put(Blocks.POLISHED_GRANITE, 0x9B6B59);
        put(Blocks.POLISHED_GRANITE_SLAB, 0x9B6B59);
        put(Blocks.POLISHED_GRANITE_STAIRS, 0x9B6B59);
        put(Blocks.POPPY, 0x8A3F26);
        put(Blocks.POTATOES, 0x46872A);
        put(Blocks.POTTED_ACACIA_SAPLING, 0x777618);
        put(Blocks.POTTED_ALLIUM, 0xA089B9);
        put(Blocks.POTTED_AZALEA, 0x647A30);
        put(Blocks.POTTED_AZURE_BLUET, 0xACCE82);
        put(Blocks.POTTED_BAMBOO, 0x5D9013);
        put(Blocks.POTTED_BIRCH_SAPLING, 0x81A251);
        put(Blocks.POTTED_BLUE_ORCHID, 0x30A3AA);
        put(Blocks.POTTED_BROWN_MUSHROOM, 0x9A755C);
        put(Blocks.POTTED_CACTUS, 0x58822D);
        put(Blocks.POTTED_CORNFLOWER, 0x507896);
        put(Blocks.POTTED_CRIMSON_FUNGUS, 0x8F2C1D);
        put(Blocks.POTTED_CRIMSON_ROOTS, 0x7F082A);
        put(Blocks.POTTED_DANDELION, 0x9EB02E);
        put(Blocks.POTTED_DARK_OAK_SAPLING, 0x3C5B1E);
        put(Blocks.POTTED_DEAD_BUSH, 0x6D5029);
        put(Blocks.POTTED_FERN, 0x7E7E7E);
        put(Blocks.POTTED_FLOWERING_AZALEA, 0x9D5CAB);
        put(Blocks.POTTED_JUNGLE_SAPLING, 0x305111);
        put(Blocks.POTTED_LILY_OF_THE_VALLEY, 0x7DB061);
        put(Blocks.POTTED_MANGROVE_PROPAGULE, 0x5FAF54);
        put(Blocks.POTTED_OAK_SAPLING, 0x4E6A29);
        put(Blocks.POTTED_ORANGE_TULIP, 0x5E8E1F);
        put(Blocks.POTTED_OXEYE_DAISY, 0xB7CC91);
        put(Blocks.POTTED_PINK_TULIP, 0x649E50);
        put(Blocks.POTTED_POPPY, 0x8A3F26);
        put(Blocks.POTTED_RED_MUSHROOM, 0xDA4740);
        put(Blocks.POTTED_RED_TULIP, 0x5B8122);
        put(Blocks.POTTED_SPRUCE_SAPLING, 0x2D3D25);
        put(Blocks.POTTED_WARPED_FUNGUS, 0x4C7059);
        put(Blocks.POTTED_WARPED_ROOTS, 0x148B7D);
        put(Blocks.POTTED_WHITE_TULIP, 0x5FA548);
        put(Blocks.POTTED_WITHER_ROSE, 0x292C17);
        put(Blocks.POWDER_SNOW, 0xF8FDFD);
        put(Blocks.POWDER_SNOW_CAULDRON, 0x4A494A);
        put(Blocks.POWERED_RAIL, 0x896B47);
        put(Blocks.PRISMARINE, 0x639C97);
        put(Blocks.PRISMARINE_BRICKS, 0x63AC9F);
        put(Blocks.PRISMARINE_BRICK_SLAB, 0x63AC9F);
        put(Blocks.PRISMARINE_BRICK_STAIRS, 0x63AC9F);
        put(Blocks.PRISMARINE_SLAB, 0x639C97);
        put(Blocks.PRISMARINE_STAIRS, 0x639C97);
        put(Blocks.PRISMARINE_WALL, 0x639C97);
        put(Blocks.PUMPKIN, 0xC57618);
        put(Blocks.PUMPKIN_STEM, 0x9B9B9B);
        put(Blocks.PURPLE_BANNER, 0x000000);
        put(Blocks.PURPLE_BED, 0x7A2AAC);
        put(Blocks.PURPLE_CANDLE, 0x6B23A0);
        put(Blocks.PURPLE_CANDLE_CAKE, 0x6B23A0);
        put(Blocks.PURPLE_CARPET, 0x7A2AAC);
        put(Blocks.PURPLE_CONCRETE, 0x64209C);
        put(Blocks.PURPLE_CONCRETE_POWDER, 0x8438B2);
        put(Blocks.PURPLE_GLAZED_TERRACOTTA, 0x6D3198);
        put(Blocks.PURPLE_SHULKER_BOX, 0x69219E);
        put(Blocks.PURPLE_STAINED_GLASS, 0x7F3FB2);
        put(Blocks.PURPLE_STAINED_GLASS_PANE, 0x7F3FB2);
        put(Blocks.PURPLE_TERRACOTTA, 0x764656);
        put(Blocks.PURPLE_WALL_BANNER, 0x000000);
        put(Blocks.PURPLE_WOOL, 0x7A2AAC);
        put(Blocks.PURPUR_BLOCK, 0xAA7EAA);
        put(Blocks.PURPUR_PILLAR, 0xAB7FAB);
        put(Blocks.PURPUR_SLAB, 0xAA7EAA);
        put(Blocks.PURPUR_STAIRS, 0xAA7EAA);
        put(Blocks.QUARTZ_BLOCK, 0xECE6DF);
        put(Blocks.QUARTZ_BRICKS, 0xEBE5DE);
        put(Blocks.QUARTZ_PILLAR, 0xECE6E0);
        put(Blocks.QUARTZ_SLAB, 0xECE6DF);
        put(Blocks.QUARTZ_STAIRS, 0xECE6DF);
        put(Blocks.RAIL, 0x7E6F56);
        put(Blocks.RAW_COPPER_BLOCK, 0x9C6A4F);
        put(Blocks.RAW_GOLD_BLOCK, 0xDDA92F);
        put(Blocks.RAW_IRON_BLOCK, 0xA6886B);
        put(Blocks.REDSTONE_BLOCK, 0xA91705);
        put(Blocks.REDSTONE_LAMP, 0x64391F);
        put(Blocks.REDSTONE_ORE, 0x8E6C6C);
        put(Blocks.REDSTONE_TORCH, 0xF85528);
        put(Blocks.REDSTONE_WALL_TORCH, 0xF855280);
        put(Blocks.RED_BANNER, 0x000000);
        put(Blocks.RED_BED, 0xC7C7C7);
        put(Blocks.RED_CANDLE, 0x9B2825);
        put(Blocks.RED_CANDLE_CAKE, 0x9B2825);
        put(Blocks.RED_CARPET, 0xA12723);
        put(Blocks.RED_CONCRETE, 0x8E2121);
        put(Blocks.RED_CONCRETE_POWDER, 0xA83633);
        put(Blocks.RED_GLAZED_TERRACOTTA, 0xB63B34);
        put(Blocks.RED_MUSHROOM, 0xDA4740);
        put(Blocks.RED_MUSHROOM_BLOCK, 0xC9302E);
        put(Blocks.RED_NETHER_BRICKS, 0x460709);
        put(Blocks.RED_NETHER_BRICK_SLAB, 0x460709);
        put(Blocks.RED_NETHER_BRICK_STAIRS, 0x460709);
        put(Blocks.RED_NETHER_BRICK_WALL, 0x460709);
        put(Blocks.RED_SAND, 0xBE6721);
        put(Blocks.RED_SANDSTONE, 0xB5621F);
        put(Blocks.RED_SANDSTONE_SLAB, 0xB5621F);
        put(Blocks.RED_SANDSTONE_STAIRS, 0xB5621F);
        put(Blocks.RED_SANDSTONE_WALL, 0xB5621F);
        put(Blocks.RED_SHULKER_BOX, 0x8F201F);
        put(Blocks.RED_STAINED_GLASS, 0x993333);
        put(Blocks.RED_STAINED_GLASS_PANE, 0x993333);
        put(Blocks.RED_TERRACOTTA, 0x8F3D2F);
        put(Blocks.RED_TULIP, 0x5B8122);
        put(Blocks.RED_WALL_BANNER, 0x000000);
        put(Blocks.RED_WOOL, 0xA12723);
        put(Blocks.REINFORCED_DEEPSLATE, 0x4D4F4C);
        put(Blocks.REPEATER, 0xA09C9B);
        put(Blocks.REPEATING_COMMAND_BLOCK, 0x8371AF);
        put(Blocks.RESPAWN_ANCHOR, 0x4C1994);
        put(Blocks.ROOTED_DIRT, 0x90674C);
        put(Blocks.ROSE_BUSH, 0x814225);
        put(Blocks.SAND, 0xDBCFA3);
        put(Blocks.SANDSTONE, 0xE0D6AA);
        put(Blocks.SANDSTONE_SLAB, 0xE0D6AA);
        put(Blocks.SANDSTONE_STAIRS, 0xE0D6AA);
        put(Blocks.SANDSTONE_WALL, 0xE0D6AA);
        put(Blocks.SCAFFOLDING, 0xAD834E);
        put(Blocks.SCULK, 0x0D1E24);
        put(Blocks.SCULK_CATALYST, 0x0F2027);
        put(Blocks.SCULK_SENSOR, 0x074654);
        put(Blocks.SCULK_SHRIEKER, 0x1B3435);
        put(Blocks.SCULK_VEIN, 0x08303A);
        put(Blocks.SEAGRASS, 0x337E08);
        put(Blocks.SEA_LANTERN, 0xB1CBC2);
        put(Blocks.SEA_PICKLE, 0x5A6126);
        put(Blocks.SHROOMLIGHT, 0xF2974C);
        put(Blocks.SHULKER_BOX, 0x8D628D);
        put(Blocks.SKELETON_SKULL, 0xB3B3B3);
        put(Blocks.SKELETON_WALL_SKULL, 0xB3B3B3);
        put(Blocks.SLIME_BLOCK, 0x6FC05B);
        put(Blocks.SMALL_AMETHYST_BUD, 0x8564C2);
        put(Blocks.SMALL_DRIPLEAF, 0x5C742E);
        put(Blocks.SMITHING_TABLE, 0x383945);
        put(Blocks.SMOKER, 0x535150);
        put(Blocks.SMOOTH_BASALT, 0x48484E);
        put(Blocks.SMOOTH_QUARTZ, 0xEDE6E0);
        put(Blocks.SMOOTH_QUARTZ_SLAB, 0xEDE6E0);
        put(Blocks.SMOOTH_QUARTZ_STAIRS, 0xEDE6E0);
        put(Blocks.SMOOTH_RED_SANDSTONE, 0xB5621F);
        put(Blocks.SMOOTH_RED_SANDSTONE_SLAB, 0xB5621F);
        put(Blocks.SMOOTH_RED_SANDSTONE_STAIRS, 0xB5621F);
        put(Blocks.SMOOTH_SANDSTONE, 0xE0D6AA);
        put(Blocks.SMOOTH_SANDSTONE_SLAB, 0xE0D6AA);
        put(Blocks.SMOOTH_SANDSTONE_STAIRS, 0xE0D6AA);
        put(Blocks.SMOOTH_STONE, 0xA1A1A1);
        put(Blocks.SMOOTH_STONE_SLAB, 0xA1A1A1);
        put(Blocks.SNOW, 0xF9FEFE);
        put(Blocks.SNOW_BLOCK, 0xF9FEFE);
        put(Blocks.SOUL_CAMPFIRE, 0x53CED2);
        put(Blocks.SOUL_FIRE, 0x34C1C5);
        put(Blocks.SOUL_LANTERN, 0x486473);
        put(Blocks.SOUL_SAND, 0x523E33);
        put(Blocks.SOUL_SOIL, 0x4C3A2F);
        put(Blocks.SOUL_TORCH, 0x6BE2E6);
        put(Blocks.SOUL_WALL_TORCH, 0x6BE2E6);
        put(Blocks.SPAWNER, 0x252F40);
        put(Blocks.SPONGE, 0xC4C14B);
        put(Blocks.SPORE_BLOSSOM, 0xD463A4);
        put(Blocks.SPRUCE_BUTTON, 0x000000);
        put(Blocks.SPRUCE_DOOR, 0x6A5030);
        put(Blocks.SPRUCE_FENCE, 0x735531);
        put(Blocks.SPRUCE_FENCE_GATE, 0x735531);
        put(Blocks.SPRUCE_LEAVES, 0x4E7A4E);
        put(Blocks.SPRUCE_LOG, 0x3B2611);
        put(Blocks.SPRUCE_PLANKS, 0x735531);
        put(Blocks.SPRUCE_PRESSURE_PLATE, 0x735531);
        put(Blocks.SPRUCE_SAPLING, 0x2D3D25);
        put(Blocks.SPRUCE_SIGN, 0x735531);
        put(Blocks.SPRUCE_SLAB, 0x735531);
        put(Blocks.SPRUCE_STAIRS, 0x735531);
        put(Blocks.SPRUCE_TRAPDOOR, 0x684F30);
        put(Blocks.SPRUCE_WALL_SIGN, 0x735531);
        put(Blocks.SPRUCE_WOOD, 0x3B2611);
        put(Blocks.STICKY_PISTON, 0x769759);
        put(Blocks.STONE, 0x7E7E7E);
        put(Blocks.STONECUTTER, 0x7A7772);
        put(Blocks.STONE_BRICKS, 0x7A7A7A);
        put(Blocks.STONE_BRICK_SLAB, 0x7A7A7A);
        put(Blocks.STONE_BRICK_STAIRS, 0x7A7A7A);
        put(Blocks.STONE_BRICK_WALL, 0x7A7A7A);
        put(Blocks.STONE_BUTTON, 0x000000);
        put(Blocks.STONE_PRESSURE_PLATE, 0x7E7E7E);
        put(Blocks.STONE_SLAB, 0x7E7E7E);
        put(Blocks.STONE_STAIRS, 0x7E7E7E);
        put(Blocks.STRIPPED_ACACIA_LOG, 0xB05D3C);
        put(Blocks.STRIPPED_ACACIA_WOOD, 0xB05D3C);
        put(Blocks.STRIPPED_BIRCH_LOG, 0xC6B177);
        put(Blocks.STRIPPED_BIRCH_WOOD, 0xC6B177);
        put(Blocks.STRIPPED_CRIMSON_HYPHAE, 0x8A3A5B);
        put(Blocks.STRIPPED_CRIMSON_STEM, 0x8A3A5B);
        put(Blocks.STRIPPED_DARK_OAK_LOG, 0x493924);
        put(Blocks.STRIPPED_DARK_OAK_WOOD, 0x493924);
        put(Blocks.STRIPPED_JUNGLE_LOG, 0xAC8555);
        put(Blocks.STRIPPED_JUNGLE_WOOD, 0xAC8555);
        put(Blocks.STRIPPED_MANGROVE_LOG, 0x783730);
        put(Blocks.STRIPPED_MANGROVE_WOOD, 0x783730);
        put(Blocks.STRIPPED_OAK_LOG, 0xB39157);
        put(Blocks.STRIPPED_OAK_WOOD, 0xB39157);
        put(Blocks.STRIPPED_SPRUCE_LOG, 0x745A35);
        put(Blocks.STRIPPED_SPRUCE_WOOD, 0x745A35);
        put(Blocks.STRIPPED_WARPED_HYPHAE, 0x3A9895);
        put(Blocks.STRIPPED_WARPED_STEM, 0x3A9895);
        put(Blocks.STRUCTURE_BLOCK, 0x5F5060);
        put(Blocks.STRUCTURE_VOID, 0x000000);
        put(Blocks.SUGAR_CANE, 0x95C165);
        put(Blocks.SUNFLOWER, 0xF6C436);
        put(Blocks.SWEET_BERRY_BUSH, 0x305E3A);
        put(Blocks.TALL_SEAGRASS, 0x2E7604);
        put(Blocks.TARGET, 0xE2AA9E);
        put(Blocks.TERRACOTTA, 0x985E44);
        put(Blocks.TINTED_GLASS, 0x2B262E);
        put(Blocks.TNT, 0x873D36);
        put(Blocks.TORCH, 0xFFD966);
        put(Blocks.TRAPPED_CHEST, 0x866025);
        put(Blocks.TRIPWIRE, 0x000000);
        put(Blocks.TRIPWIRE_HOOK, 0x000000);
        put(Blocks.TUBE_CORAL, 0x3054C5);
        put(Blocks.TUBE_CORAL_BLOCK, 0x3158CF);
        put(Blocks.TUBE_CORAL_FAN, 0x335BD1);
        put(Blocks.TUBE_CORAL_WALL_FAN, 0x335BD1);
        put(Blocks.TUFF, 0x6C6D67);
        put(Blocks.TURTLE_EGG, 0xE5E3C0);
        put(Blocks.TWISTING_VINES, 0x14907D);
        put(Blocks.TWISTING_VINES_PLANT, 0x14897A);
        put(Blocks.VERDANT_FROGLIGHT, 0xEAF6E9);
        put(Blocks.VINE, 0x747474);
        put(Blocks.VOID_AIR, 0x000000);
        put(Blocks.WALL_TORCH, 0xFFD966);
        put(Blocks.WARPED_BUTTON, 0x000000);
        put(Blocks.WARPED_DOOR, 0x2D7E78);
        put(Blocks.WARPED_FENCE, 0x2B6963);
        put(Blocks.WARPED_FENCE_GATE, 0x2B6963);
        put(Blocks.WARPED_FUNGUS, 0x4C7059);
        put(Blocks.WARPED_HYPHAE, 0x3A3B4E);
        put(Blocks.WARPED_NYLIUM, 0x2B7365);
        put(Blocks.WARPED_PLANKS, 0x2B6963);
        put(Blocks.WARPED_PRESSURE_PLATE, 0x2B6963);
        put(Blocks.WARPED_ROOTS, 0x148B7D);
        put(Blocks.WARPED_SIGN, 0x2B6963);
        put(Blocks.WARPED_SLAB, 0x2B6963);
        put(Blocks.WARPED_STAIRS, 0x2B6963);
        put(Blocks.WARPED_STEM, 0x3A3B4E);
        put(Blocks.WARPED_TRAPDOOR, 0x307A72);
        put(Blocks.WARPED_WALL_SIGN, 0x2B6963);
        put(Blocks.WARPED_WART_BLOCK, 0x177879);
        put(Blocks.WATER_CAULDRON, 0x4A494A);
        put(Blocks.WAXED_COPPER_BLOCK, 0xC06C50);
        put(Blocks.WAXED_CUT_COPPER, 0xBF6B51);
        put(Blocks.WAXED_CUT_COPPER_SLAB, 0xBF6B51);
        put(Blocks.WAXED_CUT_COPPER_STAIRS, 0xBF6B51);
        put(Blocks.WAXED_EXPOSED_COPPER, 0xA17E68);
        put(Blocks.WAXED_EXPOSED_CUT_COPPER, 0x9B7A65);
        put(Blocks.WAXED_EXPOSED_CUT_COPPER_SLAB, 0x9B7A65);
        put(Blocks.WAXED_EXPOSED_CUT_COPPER_STAIRS, 0x9B7A65);
        put(Blocks.WAXED_OXIDIZED_COPPER, 0x53A486);
        put(Blocks.WAXED_OXIDIZED_CUT_COPPER, 0x509A7F);
        put(Blocks.WAXED_OXIDIZED_CUT_COPPER_SLAB, 0x509A7F);
        put(Blocks.WAXED_OXIDIZED_CUT_COPPER_STAIRS, 0x509A7F);
        put(Blocks.WAXED_WEATHERED_COPPER, 0x6C9A6F);
        put(Blocks.WAXED_WEATHERED_CUT_COPPER, 0x6D916B);
        put(Blocks.WAXED_WEATHERED_CUT_COPPER_SLAB, 0x6D916B);
        put(Blocks.WAXED_WEATHERED_CUT_COPPER_STAIRS, 0x6D916B);
        put(Blocks.WEATHERED_COPPER, 0x6C9A6F);
        put(Blocks.WEATHERED_CUT_COPPER, 0x6D916B);
        put(Blocks.WEATHERED_CUT_COPPER_SLAB, 0x6D916B);
        put(Blocks.WEATHERED_CUT_COPPER_STAIRS, 0x6D916B);
        put(Blocks.WEEPING_VINES, 0x680100);
        put(Blocks.WEEPING_VINES_PLANT, 0x84100C);
        put(Blocks.WET_SPONGE, 0xAAB446);
        put(Blocks.WHITE_BANNER, 0x000000);
        put(Blocks.WHITE_BED, 0xEAECED);
        put(Blocks.WHITE_CANDLE, 0xD4DADB);
        put(Blocks.WHITE_CANDLE_CAKE, 0xD4DADB);
        put(Blocks.WHITE_CARPET, 0xEAECED);
        put(Blocks.WHITE_CONCRETE, 0xCFD5D6);
        put(Blocks.WHITE_CONCRETE_POWDER, 0xE2E4E4);
        put(Blocks.WHITE_GLAZED_TERRACOTTA, 0xBAD3CE);
        put(Blocks.WHITE_SHULKER_BOX, 0xDADFDF);
        put(Blocks.WHITE_STAINED_GLASS, 0xFFFFFF);
        put(Blocks.WHITE_STAINED_GLASS_PANE, 0xFFFFFF);
        put(Blocks.WHITE_TERRACOTTA, 0xD2B2A1);
        put(Blocks.WHITE_TULIP, 0x5FA548);
        put(Blocks.WHITE_WALL_BANNER, 0x000000);
        put(Blocks.WHITE_WOOL, 0xEAECED);
        put(Blocks.WITHER_ROSE, 0x292C17);
        put(Blocks.WITHER_SKELETON_SKULL, 0x323232);
        put(Blocks.WITHER_SKELETON_WALL_SKULL, 0x323232);
        put(Blocks.YELLOW_BANNER, 0x000000);
        put(Blocks.YELLOW_BED, 0xF9C628);
        put(Blocks.YELLOW_CANDLE, 0xD3A733);
        put(Blocks.YELLOW_CANDLE_CAKE, 0xD3A733);
        put(Blocks.YELLOW_CARPET, 0xF9C628);
        put(Blocks.YELLOW_CONCRETE, 0xF1AF15);
        put(Blocks.YELLOW_CONCRETE_POWDER, 0xE9C737);
        put(Blocks.YELLOW_GLAZED_TERRACOTTA, 0xECC35B);
        put(Blocks.YELLOW_SHULKER_BOX, 0xF9BE1E);
        put(Blocks.YELLOW_STAINED_GLASS, 0xE5E533);
        put(Blocks.YELLOW_STAINED_GLASS_PANE, 0xE5E533);
        put(Blocks.YELLOW_TERRACOTTA, 0xBA8523);
        put(Blocks.YELLOW_WALL_BANNER, 0x000000);
        put(Blocks.YELLOW_WOOL, 0xF9C628);
        put(Blocks.ZOMBIE_HEAD, 0x52763F);
        put(Blocks.ZOMBIE_WALL_HEAD, 0x52763F);
    }};

    @Key("settings.colors.biomes")
    @Comment("""
            Each biome has a specific color assigned to it. You can
            pick your own color here for any biomes you want to change.""")
    public static Map<ResourceKey<Biome>, Integer> BIOME_COLORS = new LinkedHashMap<>() {{
        put(Biomes.THE_VOID, 0x000000);
        put(Biomes.PLAINS, 0x8DB360);
        put(Biomes.SUNFLOWER_PLAINS, 0xB5DB88);
        put(Biomes.SNOWY_PLAINS, 0xFFFFFF);
        put(Biomes.ICE_SPIKES, 0xB4DCDC);
        put(Biomes.DESERT, 0xFA9418);
        put(Biomes.SWAMP, 0x07F9B2);
        put(Biomes.MANGROVE_SWAMP, 0x67352B);
        put(Biomes.FOREST, 0x056621);
        put(Biomes.FLOWER_FOREST, 0x2D8E49);
        put(Biomes.BIRCH_FOREST, 0x307444);
        put(Biomes.DARK_FOREST, 0x40511A);
        put(Biomes.OLD_GROWTH_BIRCH_FOREST, 0x307444);
        put(Biomes.OLD_GROWTH_PINE_TAIGA, 0x596651);
        put(Biomes.OLD_GROWTH_SPRUCE_TAIGA, 0x818E79);
        put(Biomes.TAIGA, 0x0B6659);
        put(Biomes.SNOWY_TAIGA, 0x31554A);
        put(Biomes.SAVANNA, 0xBDB25F);
        put(Biomes.SAVANNA_PLATEAU, 0xA79D64);
        put(Biomes.WINDSWEPT_HILLS, 0x597D72);
        put(Biomes.WINDSWEPT_GRAVELLY_HILLS, 0x789878);
        put(Biomes.WINDSWEPT_FOREST, 0x589C6C);
        put(Biomes.WINDSWEPT_SAVANNA, 0xE5DA87);
        put(Biomes.JUNGLE, 0x537B09);
        put(Biomes.SPARSE_JUNGLE, 0x628B17);
        put(Biomes.BAMBOO_JUNGLE, 0x768E14);
        put(Biomes.BADLANDS, 0xD94515);
        put(Biomes.ERODED_BADLANDS, 0xFF6D3D);
        put(Biomes.WOODED_BADLANDS, 0xB09765);
        put(Biomes.MEADOW, 0x2C4205);
        put(Biomes.GROVE, 0x888888);
        put(Biomes.SNOWY_SLOPES, 0xA0A0A0);
        put(Biomes.FROZEN_PEAKS, 0xA0A0A0);
        put(Biomes.JAGGED_PEAKS, 0xA0A0A0);
        put(Biomes.STONY_PEAKS, 0x888888);
        put(Biomes.RIVER, 0x0000FF);
        put(Biomes.FROZEN_RIVER, 0xA0A0FF);
        put(Biomes.BEACH, 0xFADE55);
        put(Biomes.SNOWY_BEACH, 0xFAF0C0);
        put(Biomes.STONY_SHORE, 0xA2A284);
        put(Biomes.WARM_OCEAN, 0x0000AC);
        put(Biomes.LUKEWARM_OCEAN, 0x000090);
        put(Biomes.DEEP_LUKEWARM_OCEAN, 0x000040);
        put(Biomes.OCEAN, 0x000070);
        put(Biomes.DEEP_OCEAN, 0x000030);
        put(Biomes.COLD_OCEAN, 0x202070);
        put(Biomes.DEEP_COLD_OCEAN, 0x202038);
        put(Biomes.FROZEN_OCEAN, 0x7070D6);
        put(Biomes.DEEP_FROZEN_OCEAN, 0x404090);
        put(Biomes.MUSHROOM_FIELDS, 0xFF00FF);
        put(Biomes.DRIPSTONE_CAVES, 0x888888);
        put(Biomes.LUSH_CAVES, 0x7BA331);
        put(Biomes.DEEP_DARK, 0x0E252A);
        put(Biomes.NETHER_WASTES, 0xBF3B3B);
        put(Biomes.WARPED_FOREST, 0x49907B);
        put(Biomes.CRIMSON_FOREST, 0xDD0808);
        put(Biomes.SOUL_SAND_VALLEY, 0x5E3830);
        put(Biomes.BASALT_DELTAS, 0x403636);
        put(Biomes.THE_END, 0x8080FF);
        put(Biomes.END_HIGHLANDS, 0x8080FF);
        put(Biomes.END_MIDLANDS, 0x8080FF);
        put(Biomes.SMALL_END_ISLANDS, 0x8080FF);
        put(Biomes.END_BARRENS, 0x8080FF);
    }};
    @Key("settings.color-overrides.biomes.grass")
    @Comment("Override grass colors per biome")
    public static Map<ResourceKey<Biome>, Integer> COLOR_OVERRIDES_BIOME_GRASS = new LinkedHashMap<>();

    @Key("settings.color-overrides.biomes.foliage")
    @Comment("Override foliage (plant) colors per biome")
    public static Map<ResourceKey<Biome>, Integer> COLOR_OVERRIDES_BIOME_FOLIAGE = new LinkedHashMap<>() {{
        put(Biomes.DARK_FOREST, 0x1c7b07);
        put(Biomes.JUNGLE, 0x1f8907);
        put(Biomes.BAMBOO_JUNGLE, 0x1f8907);
        put(Biomes.SPARSE_JUNGLE, 0x1f8907);
    }};

    @Key("settings.color-overrides.biomes.water")
    @Comment("Override water colors per biome")
    public static Map<ResourceKey<Biome>, Integer> COLOR_OVERRIDES_BIOME_WATER = new LinkedHashMap<>();

    private static final AdvancedConfig CONFIG = new AdvancedConfig();

    public static void reload() {
        // this has to extract before advanced config to load biome colors correctly
        FileUtil.extract("/web/", World.WEB_DIR, !Config.WEB_DIR_READONLY);

        CONFIG.reload(FileUtil.MAIN_DIR.resolve("advanced.yml"), AdvancedConfig.class);
    }

    protected Object getValue(String path, Object def) {
        if (getConfig().get(path) == null) {
            // only set if this path is empty
            if (def instanceof Map<?, ?> map && !map.isEmpty()) {
                // turn into strings
                map.forEach((k, v) -> {
                    String key, hex = Colors.toHex((int) v);
                    if (k instanceof Block block) {
                        key = Registry.BLOCK.getKey(block).toString();
                    } else {
                        key = ((ResourceKey<?>) k).location().toString();
                    }
                    getConfig().set(path + "." + key, hex);
                });
            } else {
                // regular usage
                getConfig().set(path, def);
            }
        }

        Object value = getConfig().get(path);

        if (value instanceof MemorySection) {
            // convert back to objects
            Map<Object, Object> sanitized = new LinkedHashMap<>();
            ConfigurationSection section = getConfig().getConfigurationSection(path);
            if (section != null) {
                Registry<Biome> registry = BiomeColors.getBiomeRegistry();
                for (String key : section.getKeys(false)) {
                    String hex = section.getString(key);
                    if (hex == null) {
                        continue;
                    }
                    ResourceLocation resource = new ResourceLocation(key);
                    Biome biome = registry.get(resource);
                    ResourceKey<Biome> resourceKey = biome == null ? null : registry.getResourceKey(biome).orElse(null);
                    sanitized.put(resourceKey != null ? resourceKey : Registry.BLOCK.get(resource), Colors.fromHex(hex));
                }
            }
            value = sanitized;
        }

        return value;
    }
}
