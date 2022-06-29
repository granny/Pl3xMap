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
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

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

    @Key("settings.colors.blocks")
    @Comment("""
            Each block has a specific color assigned to it. You can
            pick your own color here for any blocks you want to change.
            Any blocks _not_ in this list will use Mojang's color.""")
    public static Map<Block, Integer> BLOCK_COLORS = new LinkedHashMap<>() {{
        put(Blocks.LAVA, 0xEA5C0F);

        put(Blocks.ORANGE_TULIP, 0xBD6A22);
        put(Blocks.PINK_TULIP, 0xEBC5FD);
        put(Blocks.RED_TULIP, 0x9B221A);
        put(Blocks.WHITE_TULIP, 0xD6E8E8);

        put(Blocks.WHEAT, 0xDCBB65);
        put(Blocks.ATTACHED_MELON_STEM, 0xE0C71C);
        put(Blocks.ATTACHED_PUMPKIN_STEM, 0xE0C71C);

        put(Blocks.POTTED_ALLIUM, 0xB878ED);
        put(Blocks.POTTED_AZURE_BLUET, 0xF7F7F7);
        put(Blocks.POTTED_BLUE_ORCHID, 0x2ABFFD);
        put(Blocks.POTTED_CORNFLOWER, 0x466AEB);
        put(Blocks.POTTED_DANDELION, 0xFFEC4F);
        put(Blocks.POTTED_LILY_OF_THE_VALLEY, 0xFFFFFF);
        put(Blocks.POTTED_ORANGE_TULIP, 0xBD6A22);
        put(Blocks.POTTED_OXEYE_DAISY, 0xD6E8E8);
        put(Blocks.POTTED_PINK_TULIP, 0xEBC5FD);
        put(Blocks.POTTED_POPPY, 0xED302C);
        put(Blocks.POTTED_RED_TULIP, 0x9B221A);
        put(Blocks.POTTED_WHITE_TULIP, 0xD6E8E8);
        put(Blocks.POTTED_WITHER_ROSE, 0x211A16);

        put(Blocks.POTTED_OAK_SAPLING, 0x000000);
        put(Blocks.POTTED_SPRUCE_SAPLING, 0x000000);
        put(Blocks.POTTED_BIRCH_SAPLING, 0x000000);
        put(Blocks.POTTED_JUNGLE_SAPLING, 0x000000);
        put(Blocks.POTTED_ACACIA_SAPLING, 0x000000);
        put(Blocks.POTTED_DARK_OAK_SAPLING, 0x000000);
        put(Blocks.POTTED_FERN, 0x000000);
        put(Blocks.POTTED_RED_MUSHROOM, 0x000000);
        put(Blocks.POTTED_BROWN_MUSHROOM, 0x000000);
        put(Blocks.POTTED_DEAD_BUSH, 0x000000);
        put(Blocks.POTTED_CACTUS, 0x000000);
        put(Blocks.POTTED_BAMBOO, 0x000000);
        put(Blocks.POTTED_CRIMSON_FUNGUS, 0x000000);
        put(Blocks.POTTED_WARPED_FUNGUS, 0x000000);
        put(Blocks.POTTED_CRIMSON_ROOTS, 0x000000);
        put(Blocks.POTTED_WARPED_ROOTS, 0x000000);
        put(Blocks.POTTED_AZALEA, 0x000000);
        put(Blocks.POTTED_FLOWERING_AZALEA, 0x000000);

        put(Blocks.POWERED_RAIL, 0x000000);
        put(Blocks.DETECTOR_RAIL, 0x000000);
        put(Blocks.RAIL, 0x000000);
        put(Blocks.ACTIVATOR_RAIL, 0x000000);

        put(Blocks.TORCH, 0x000000);
        put(Blocks.WALL_TORCH, 0x000000);
        put(Blocks.LADDER, 0x000000);
        put(Blocks.LEVER, 0x000000);
        put(Blocks.REDSTONE_TORCH, 0x000000);
        put(Blocks.REDSTONE_WALL_TORCH, 0x000000);
        put(Blocks.STONE_BUTTON, 0x000000);
        put(Blocks.SOUL_TORCH, 0x000000);
        put(Blocks.SOUL_WALL_TORCH, 0x000000);
        put(Blocks.REPEATER, 0x000000);
        put(Blocks.TRIPWIRE_HOOK, 0x000000);
        put(Blocks.TRIPWIRE, 0x000000);
        put(Blocks.COMPARATOR, 0x000000);

        put(Blocks.OAK_BUTTON, 0x000000);
        put(Blocks.SPRUCE_BUTTON, 0x000000);
        put(Blocks.BIRCH_BUTTON, 0x000000);
        put(Blocks.JUNGLE_BUTTON, 0x000000);
        put(Blocks.ACACIA_BUTTON, 0x000000);
        put(Blocks.DARK_OAK_BUTTON, 0x000000);
        put(Blocks.CRIMSON_BUTTON, 0x000000);
        put(Blocks.WARPED_BUTTON, 0x000000);
        put(Blocks.POLISHED_BLACKSTONE_BUTTON, 0x000000);
        put(Blocks.SKELETON_SKULL, 0x000000);
        put(Blocks.SKELETON_WALL_SKULL, 0x000000);
        put(Blocks.WITHER_SKELETON_SKULL, 0x000000);
        put(Blocks.WITHER_SKELETON_WALL_SKULL, 0x000000);
        put(Blocks.ZOMBIE_HEAD, 0x000000);
        put(Blocks.ZOMBIE_WALL_HEAD, 0x000000);
        put(Blocks.PLAYER_HEAD, 0x000000);
        put(Blocks.PLAYER_WALL_HEAD, 0x000000);
        put(Blocks.CREEPER_HEAD, 0x000000);
        put(Blocks.CREEPER_WALL_HEAD, 0x000000);
        put(Blocks.DRAGON_HEAD, 0x000000);
        put(Blocks.DRAGON_WALL_HEAD, 0x000000);
        put(Blocks.END_ROD, 0x000000);
        put(Blocks.SCAFFOLDING, 0x000000);
        put(Blocks.CANDLE, 0x000000);
        put(Blocks.WHITE_CANDLE, 0x000000);
        put(Blocks.ORANGE_CANDLE, 0x000000);
        put(Blocks.MAGENTA_CANDLE, 0x000000);
        put(Blocks.LIGHT_BLUE_CANDLE, 0x000000);
        put(Blocks.YELLOW_CANDLE, 0x000000);
        put(Blocks.LIME_CANDLE, 0x000000);
        put(Blocks.PINK_CANDLE, 0x000000);
        put(Blocks.GRAY_CANDLE, 0x000000);
        put(Blocks.LIGHT_GRAY_CANDLE, 0x000000);
        put(Blocks.CYAN_CANDLE, 0x000000);
        put(Blocks.PURPLE_CANDLE, 0x000000);
        put(Blocks.BLUE_CANDLE, 0x000000);
        put(Blocks.BROWN_CANDLE, 0x000000);
        put(Blocks.GREEN_CANDLE, 0x000000);
        put(Blocks.RED_CANDLE, 0x000000);
        put(Blocks.BLACK_CANDLE, 0x000000);

        put(Blocks.FLOWER_POT, 0x000000);

        put(Blocks.ALLIUM, 0xB878ED);
        put(Blocks.AZURE_BLUET, 0xF7F7F7);
        put(Blocks.BLUE_ORCHID, 0x2ABFFD);
        put(Blocks.CORNFLOWER, 0x466AEB);
        put(Blocks.DANDELION, 0xFFEC4F);
        put(Blocks.LILY_OF_THE_VALLEY, 0xFFFFFF);
        put(Blocks.OXEYE_DAISY, 0xD6E8E8);
        put(Blocks.POPPY, 0xED302C);
        put(Blocks.WITHER_ROSE, 0x211A16);

        put(Blocks.LILAC, 0xB66BB2);
        put(Blocks.PEONY, 0xEBC5FD);
        put(Blocks.ROSE_BUSH, 0x9B221A);
        put(Blocks.SUNFLOWER, 0xFFEC4F);

        put(Blocks.LILY_PAD, 0x208030);

        put(Blocks.GRASS, 0x000000);
        put(Blocks.TALL_GRASS, 0x000000);

        put(Blocks.GLASS, 0xFFFFFF);
        put(Blocks.MYCELIUM, 0x6F6265);
        put(Blocks.TERRACOTTA, 0x9E6246);

        put(Blocks.BIRCH_LEAVES, 0x668644); // 25% darker than normal
        put(Blocks.SPRUCE_LEAVES, 0x4e7a4e); // 25% darker than normal
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

    private static final Advanced CONFIG = new Advanced();

    public static void reload() {
        CONFIG.reload(FileUtil.PLUGIN_DIR.resolve("advanced.yml"), Advanced.class);
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
                Registry<Biome> registry = BiomeColors.getBiomeRegistry(MinecraftServer.getServer().getAllLevels().iterator().next());
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
