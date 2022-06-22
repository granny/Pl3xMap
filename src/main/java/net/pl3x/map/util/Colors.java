package net.pl3x.map.util;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.pl3x.map.world.MapWorld;

import java.util.Map;

public class Colors {
    public static int lerpRGB(int color0, int color1, float delta) {
        if (color0 == color1) return color0;
        if (delta >= 1F) return color1;
        if (delta <= 0F) return color0;
        return rgb(
                (int) Mathf.lerp(red(color0), red(color1), delta),
                (int) Mathf.lerp(green(color0), green(color1), delta),
                (int) Mathf.lerp(blue(color0), blue(color1), delta)
        );
    }

    public static int lerpARGB(int color0, int color1, float delta) {
        if (color0 == color1) return color0;
        if (delta >= 1F) return color1;
        if (delta <= 0F) return color0;
        return argb(
                (int) Mathf.lerp(alpha(color0), alpha(color1), delta),
                (int) Mathf.lerp(red(color0), red(color1), delta),
                (int) Mathf.lerp(green(color0), green(color1), delta),
                (int) Mathf.lerp(blue(color0), blue(color1), delta)
        );
    }

    public static int alpha(int argb) {
        return argb >> 24 & 0xFF;
    }

    public static int red(int argb) {
        return argb >> 16 & 0xFF;
    }

    public static int green(int argb) {
        return argb >> 8 & 0xFF;
    }

    public static int blue(int argb) {
        return argb & 0xFF;
    }

    public static int rgb(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
    }

    public static int mix(int color0, int color1) {
        int a = alpha(color0);
        if (a == 0) {
            return 0;
        }
        float ratio = alpha(color1) / 255F;
        float iRatio = 1F - ratio;
        int r = (int) ((red(color0) * iRatio) + (red(color1) * ratio));
        int g = (int) ((green(color0) * iRatio) + (green(color1) * ratio));
        int b = (int) ((blue(color0) * iRatio) + (blue(color1) * ratio));
        return (a << 24 | r << 16 | g << 8 | b);
    }

    public static int setAlpha(int alpha, int color) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    public static int fromHex(String color) {
        return (int) Long.parseLong(color.replace("#", ""), 16);
    }

    public static int getBlockColor(MapWorld mapWorld, Biome biome, BlockState state, BlockPos pos) {
        Block block = state.getBlock();
        int color = blockColors.getOrDefault(block, -1);

        if (color != 0) {
            if (block == Blocks.MELON_STEM || block == Blocks.PUMPKIN_STEM) {
                int age = state.getValue(StemBlock.AGE);
                color = rgb(age * 32, 0xFF - age * 8, age * 4);
            } else if (block == Blocks.WHEAT) {
                color = Colors.lerpRGB(MaterialColor.PLANT.col, color, (state.getValue(CropBlock.AGE) + 1) / 8F);
            } else if (block == Blocks.REDSTONE_WIRE) {
                color = RedStoneWireBlock.getColorForPower(state.getValue(RedStoneWireBlock.POWER));
            } else if (isGrass(block)) {
                color = biome == null ? -1 : mapWorld.getBiomeColors().getGrassColor(biome);
            } else if (isFoliage(block)) {
                color = biome == null ? -1 : mapWorld.getBiomeColors().getFoliageColor(biome);
            }
        }

        if (color < 0) {
            return state.getMapColor(mapWorld.getLevel(), pos).col;
        }

        return color;
    }

    public static int getWaterColor(MapWorld mapWorld, Biome biome) {
        return biome == null ? MaterialColor.WATER.col : mapWorld.getBiomeColors().getWaterColor(biome);
    }

    public static boolean isGrass(Block block) {
        return block == Blocks.GRASS_BLOCK || block == Blocks.GRASS || block == Blocks.TALL_GRASS || block == Blocks.FERN || block == Blocks.LARGE_FERN || block == Blocks.POTTED_FERN || block == Blocks.SUGAR_CANE;
    }

    public static boolean isFoliage(Block block) {
        return block == Blocks.VINE || block == Blocks.OAK_LEAVES || block == Blocks.JUNGLE_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.DARK_OAK_LEAVES;
    }

    public static final Map<Block, Integer> blockColors = Map.ofEntries(
            Map.entry(Blocks.LAVA, 0xEA5C0F),

            Map.entry(Blocks.ORANGE_TULIP, 0xBD6A22),
            Map.entry(Blocks.PINK_TULIP, 0xEBC5FD),
            Map.entry(Blocks.RED_TULIP, 0x9B221A),
            Map.entry(Blocks.WHITE_TULIP, 0xD6E8E8),

            Map.entry(Blocks.WHEAT, 0xDCBB65),
            Map.entry(Blocks.ATTACHED_MELON_STEM, 0xE0C71C),
            Map.entry(Blocks.ATTACHED_PUMPKIN_STEM, 0xE0C71C),

            Map.entry(Blocks.POTTED_ALLIUM, 0xB878ED),
            Map.entry(Blocks.POTTED_AZURE_BLUET, 0xF7F7F7),
            Map.entry(Blocks.POTTED_BLUE_ORCHID, 0x2ABFFD),
            Map.entry(Blocks.POTTED_CORNFLOWER, 0x466AEB),
            Map.entry(Blocks.POTTED_DANDELION, 0xFFEC4F),
            Map.entry(Blocks.POTTED_LILY_OF_THE_VALLEY, 0xFFFFFF),
            Map.entry(Blocks.POTTED_ORANGE_TULIP, 0xBD6A22),
            Map.entry(Blocks.POTTED_OXEYE_DAISY, 0xD6E8E8),
            Map.entry(Blocks.POTTED_PINK_TULIP, 0xEBC5FD),
            Map.entry(Blocks.POTTED_POPPY, 0xED302C),
            Map.entry(Blocks.POTTED_RED_TULIP, 0x9B221A),
            Map.entry(Blocks.POTTED_WHITE_TULIP, 0xD6E8E8),
            Map.entry(Blocks.POTTED_WITHER_ROSE, 0x211A16),

            Map.entry(Blocks.POTTED_OAK_SAPLING, 0x0),
            Map.entry(Blocks.POTTED_SPRUCE_SAPLING, 0x0),
            Map.entry(Blocks.POTTED_BIRCH_SAPLING, 0x0),
            Map.entry(Blocks.POTTED_JUNGLE_SAPLING, 0x0),
            Map.entry(Blocks.POTTED_ACACIA_SAPLING, 0x0),
            Map.entry(Blocks.POTTED_DARK_OAK_SAPLING, 0x0),
            Map.entry(Blocks.POTTED_FERN, 0x0),
            Map.entry(Blocks.POTTED_RED_MUSHROOM, 0x0),
            Map.entry(Blocks.POTTED_BROWN_MUSHROOM, 0x0),
            Map.entry(Blocks.POTTED_DEAD_BUSH, 0x0),
            Map.entry(Blocks.POTTED_CACTUS, 0x0),
            Map.entry(Blocks.POTTED_BAMBOO, 0x0),
            Map.entry(Blocks.POTTED_CRIMSON_FUNGUS, 0x0),
            Map.entry(Blocks.POTTED_WARPED_FUNGUS, 0x0),
            Map.entry(Blocks.POTTED_CRIMSON_ROOTS, 0x0),
            Map.entry(Blocks.POTTED_WARPED_ROOTS, 0x0),
            Map.entry(Blocks.POTTED_AZALEA, 0x0),
            Map.entry(Blocks.POTTED_FLOWERING_AZALEA, 0x0),

            Map.entry(Blocks.POWERED_RAIL, 0x0),
            Map.entry(Blocks.DETECTOR_RAIL, 0x0),
            Map.entry(Blocks.RAIL, 0x0),
            Map.entry(Blocks.ACTIVATOR_RAIL, 0x0),

            Map.entry(Blocks.TORCH, 0x0),
            Map.entry(Blocks.WALL_TORCH, 0x0),
            Map.entry(Blocks.LADDER, 0x0),
            Map.entry(Blocks.LEVER, 0x0),
            Map.entry(Blocks.REDSTONE_TORCH, 0x0),
            Map.entry(Blocks.REDSTONE_WALL_TORCH, 0x0),
            Map.entry(Blocks.STONE_BUTTON, 0x0),
            Map.entry(Blocks.SOUL_TORCH, 0x0),
            Map.entry(Blocks.SOUL_WALL_TORCH, 0x0),
            Map.entry(Blocks.REPEATER, 0x0),
            Map.entry(Blocks.TRIPWIRE_HOOK, 0x0),
            Map.entry(Blocks.TRIPWIRE, 0x0),
            Map.entry(Blocks.COMPARATOR, 0x0),

            Map.entry(Blocks.OAK_BUTTON, 0x0),
            Map.entry(Blocks.SPRUCE_BUTTON, 0x0),
            Map.entry(Blocks.BIRCH_BUTTON, 0x0),
            Map.entry(Blocks.JUNGLE_BUTTON, 0x0),
            Map.entry(Blocks.ACACIA_BUTTON, 0x0),
            Map.entry(Blocks.DARK_OAK_BUTTON, 0x0),
            Map.entry(Blocks.CRIMSON_BUTTON, 0x0),
            Map.entry(Blocks.WARPED_BUTTON, 0x0),
            Map.entry(Blocks.POLISHED_BLACKSTONE_BUTTON, 0x0),
            Map.entry(Blocks.SKELETON_SKULL, 0x0),
            Map.entry(Blocks.SKELETON_WALL_SKULL, 0x0),
            Map.entry(Blocks.WITHER_SKELETON_SKULL, 0x0),
            Map.entry(Blocks.WITHER_SKELETON_WALL_SKULL, 0x0),
            Map.entry(Blocks.ZOMBIE_HEAD, 0x0),
            Map.entry(Blocks.ZOMBIE_WALL_HEAD, 0x0),
            Map.entry(Blocks.PLAYER_HEAD, 0x0),
            Map.entry(Blocks.PLAYER_WALL_HEAD, 0x0),
            Map.entry(Blocks.CREEPER_HEAD, 0x0),
            Map.entry(Blocks.CREEPER_WALL_HEAD, 0x0),
            Map.entry(Blocks.DRAGON_HEAD, 0x0),
            Map.entry(Blocks.DRAGON_WALL_HEAD, 0x0),
            Map.entry(Blocks.END_ROD, 0x0),
            Map.entry(Blocks.SCAFFOLDING, 0x0),
            Map.entry(Blocks.CANDLE, 0x0),
            Map.entry(Blocks.WHITE_CANDLE, 0x0),
            Map.entry(Blocks.ORANGE_CANDLE, 0x0),
            Map.entry(Blocks.MAGENTA_CANDLE, 0x0),
            Map.entry(Blocks.LIGHT_BLUE_CANDLE, 0x0),
            Map.entry(Blocks.YELLOW_CANDLE, 0x0),
            Map.entry(Blocks.LIME_CANDLE, 0x0),
            Map.entry(Blocks.PINK_CANDLE, 0x0),
            Map.entry(Blocks.GRAY_CANDLE, 0x0),
            Map.entry(Blocks.LIGHT_GRAY_CANDLE, 0x0),
            Map.entry(Blocks.CYAN_CANDLE, 0x0),
            Map.entry(Blocks.PURPLE_CANDLE, 0x0),
            Map.entry(Blocks.BLUE_CANDLE, 0x0),
            Map.entry(Blocks.BROWN_CANDLE, 0x0),
            Map.entry(Blocks.GREEN_CANDLE, 0x0),
            Map.entry(Blocks.RED_CANDLE, 0x0),
            Map.entry(Blocks.BLACK_CANDLE, 0x0),

            Map.entry(Blocks.FLOWER_POT, 0x0),

            Map.entry(Blocks.ALLIUM, 0xB878ED),
            Map.entry(Blocks.AZURE_BLUET, 0xF7F7F7),
            Map.entry(Blocks.BLUE_ORCHID, 0x2ABFFD),
            Map.entry(Blocks.CORNFLOWER, 0x466AEB),
            Map.entry(Blocks.DANDELION, 0xFFEC4F),
            Map.entry(Blocks.LILY_OF_THE_VALLEY, 0xFFFFFF),
            Map.entry(Blocks.OXEYE_DAISY, 0xD6E8E8),
            Map.entry(Blocks.POPPY, 0xED302C),
            Map.entry(Blocks.WITHER_ROSE, 0x211A16),

            Map.entry(Blocks.LILAC, 0xB66BB2),
            Map.entry(Blocks.PEONY, 0xEBC5FD),
            Map.entry(Blocks.ROSE_BUSH, 0x9B221A),
            Map.entry(Blocks.SUNFLOWER, 0xFFEC4F),

            Map.entry(Blocks.LILY_PAD, 0x208030),

            Map.entry(Blocks.GRASS, 0x0),
            Map.entry(Blocks.TALL_GRASS, 0x0),

            Map.entry(Blocks.GLASS, 0xFFFFFF),
            Map.entry(Blocks.MYCELIUM, 0x6F6265),
            Map.entry(Blocks.TERRACOTTA, 0x9E6246),

            Map.entry(Blocks.BIRCH_LEAVES, 0x668644), // 25% darker than normal
            Map.entry(Blocks.SPRUCE_LEAVES, 0x4e7a4e) // 25% darker than normal
    );

    public static final Map<ResourceKey<Biome>, Integer> biomeColors = Map.ofEntries(
            Map.entry(Biomes.THE_VOID, 0x00000000),
            Map.entry(Biomes.PLAINS, 0xFF8DB360),
            Map.entry(Biomes.SUNFLOWER_PLAINS, 0xFFB5DB88),
            Map.entry(Biomes.SNOWY_PLAINS, 0xFFFFFFFF),
            Map.entry(Biomes.ICE_SPIKES, 0xFFB4DCDC),
            Map.entry(Biomes.DESERT, 0xFFFA9418),
            Map.entry(Biomes.SWAMP, 0xFF07F9B2),
            Map.entry(Biomes.MANGROVE_SWAMP, 0x67352B),
            Map.entry(Biomes.FOREST, 0xFF056621),
            Map.entry(Biomes.FLOWER_FOREST, 0xFF2D8E49),
            Map.entry(Biomes.BIRCH_FOREST, 0xFF307444),
            Map.entry(Biomes.DARK_FOREST, 0xFF40511A),
            Map.entry(Biomes.OLD_GROWTH_BIRCH_FOREST, 0xFF307444),
            Map.entry(Biomes.OLD_GROWTH_PINE_TAIGA, 0xFF596651),
            Map.entry(Biomes.OLD_GROWTH_SPRUCE_TAIGA, 0xFF818E79),
            Map.entry(Biomes.TAIGA, 0xFF0B6659),
            Map.entry(Biomes.SNOWY_TAIGA, 0xFF31554A),
            Map.entry(Biomes.SAVANNA, 0xFFBDB25F),
            Map.entry(Biomes.SAVANNA_PLATEAU, 0xFFA79D64),
            Map.entry(Biomes.WINDSWEPT_HILLS, 0xFF597D72),
            Map.entry(Biomes.WINDSWEPT_GRAVELLY_HILLS, 0xFF789878),
            Map.entry(Biomes.WINDSWEPT_FOREST, 0xFF589C6C),
            Map.entry(Biomes.WINDSWEPT_SAVANNA, 0xFFE5DA87),
            Map.entry(Biomes.JUNGLE, 0xFF537B09),
            Map.entry(Biomes.SPARSE_JUNGLE, 0xFF628B17),
            Map.entry(Biomes.BAMBOO_JUNGLE, 0xFF768E14),
            Map.entry(Biomes.BADLANDS, 0xFFD94515),
            Map.entry(Biomes.ERODED_BADLANDS, 0xFFFF6D3D),
            Map.entry(Biomes.WOODED_BADLANDS, 0xFFB09765),
            Map.entry(Biomes.MEADOW, 0xFF2C4205),
            Map.entry(Biomes.GROVE, 0xFF888888),
            Map.entry(Biomes.SNOWY_SLOPES, 0xFFA0A0A0),
            Map.entry(Biomes.FROZEN_PEAKS, 0xFFA0A0A0),
            Map.entry(Biomes.JAGGED_PEAKS, 0xFFA0A0A0),
            Map.entry(Biomes.STONY_PEAKS, 0xFF888888),
            Map.entry(Biomes.RIVER, 0xFF0000FF),
            Map.entry(Biomes.FROZEN_RIVER, 0xFFA0A0FF),
            Map.entry(Biomes.BEACH, 0xFFFADE55),
            Map.entry(Biomes.SNOWY_BEACH, 0xFFFAF0C0),
            Map.entry(Biomes.STONY_SHORE, 0xFFA2A284),
            Map.entry(Biomes.WARM_OCEAN, 0xFF0000AC),
            Map.entry(Biomes.LUKEWARM_OCEAN, 0xFF000090),
            Map.entry(Biomes.DEEP_LUKEWARM_OCEAN, 0xFF000040),
            Map.entry(Biomes.OCEAN, 0xFF000070),
            Map.entry(Biomes.DEEP_OCEAN, 0xFF000030),
            Map.entry(Biomes.COLD_OCEAN, 0xFF202070),
            Map.entry(Biomes.DEEP_COLD_OCEAN, 0xFF202038),
            Map.entry(Biomes.FROZEN_OCEAN, 0xFF7070D6),
            Map.entry(Biomes.DEEP_FROZEN_OCEAN, 0xFF404090),
            Map.entry(Biomes.MUSHROOM_FIELDS, 0xFFFF00FF),
            Map.entry(Biomes.DRIPSTONE_CAVES, 0xFF888888),
            Map.entry(Biomes.LUSH_CAVES, 0xFF7BA331),
            Map.entry(Biomes.DEEP_DARK, 0x0E252A),
            Map.entry(Biomes.NETHER_WASTES, 0xFFBF3B3B),
            Map.entry(Biomes.WARPED_FOREST, 0xFF49907B),
            Map.entry(Biomes.CRIMSON_FOREST, 0xFFDD0808),
            Map.entry(Biomes.SOUL_SAND_VALLEY, 0xFF5E3830),
            Map.entry(Biomes.BASALT_DELTAS, 0xFF403636),
            Map.entry(Biomes.THE_END, 0xFF8080FF),
            Map.entry(Biomes.END_HIGHLANDS, 0xFF8080FF),
            Map.entry(Biomes.END_MIDLANDS, 0xFF8080FF),
            Map.entry(Biomes.SMALL_END_ISLANDS, 0xFF8080FF),
            Map.entry(Biomes.END_BARRENS, 0xFF8080FF)
    );
}
