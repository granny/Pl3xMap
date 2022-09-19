package net.pl3x.map.util;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import javax.imageio.ImageIO;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.pl3x.map.configuration.AdvancedConfig;
import net.pl3x.map.coordinate.BlockCoordinate;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.world.World;

public class BiomeColors {
    private static final Set<Block> grassColorBlocks = ImmutableSet.of(
            Blocks.GRASS_BLOCK,
            Blocks.GRASS,
            Blocks.TALL_GRASS,
            Blocks.FERN,
            Blocks.LARGE_FERN,
            Blocks.POTTED_FERN,
            Blocks.SUGAR_CANE
    );

    private static final Set<Block> foliageColorBlocks = ImmutableSet.of(
            Blocks.VINE,
            Blocks.OAK_LEAVES,
            Blocks.JUNGLE_LEAVES,
            Blocks.ACACIA_LEAVES,
            Blocks.DARK_OAK_LEAVES
    );

    private static final Set<Block> waterColorBlocks = ImmutableSet.of(
            Blocks.WATER,
            Blocks.BUBBLE_COLUMN,
            Blocks.WATER_CAULDRON
    );

    private static final Set<Material> waterColorMaterials = ImmutableSet.of(
            Material.WATER_PLANT,
            Material.REPLACEABLE_WATER_PLANT
    );

    private static final int[] mapGrass;
    private static final int[] mapFoliage;

    static {
        Path imagesDir = World.WEB_DIR.resolve("images");
        BufferedImage imgGrass, imgFoliage;

        try {
            imgGrass = ImageIO.read(imagesDir.resolve("grass.png").toFile());
            imgFoliage = ImageIO.read(imagesDir.resolve("foliage.png").toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read biome images", e);
        }

        mapGrass = getColorsFromImage(imgGrass);
        mapFoliage = getColorsFromImage(imgFoliage);
    }

    private final Map<Biome, Integer> grassColors = new HashMap<>();
    private final Map<Biome, Integer> foliageColors = new HashMap<>();
    private final Map<Biome, Integer> waterColors = new HashMap<>();

    private final World world;

    public BiomeColors(World world) {
        this.world = world;
        for (Biome biome : world.getBiomeRegistry()) {
            float temperature = Mathf.clamp(0.0F, 1.0F, biome.getBaseTemperature());
            float humidity = Mathf.clamp(0.0F, 1.0F, biome.getDownfall());
            grassColors.put(biome, biome.getSpecialEffects().getGrassColorOverride()
                    .orElse(getDefaultGrassColor(temperature, humidity)));
            foliageColors.put(biome, biome.getSpecialEffects().getFoliageColorOverride()
                    .orElse(getDefaultFoliageColor(temperature, humidity)));
            waterColors.put(biome, biome.getSpecialEffects().getWaterColor());
        }

        AdvancedConfig.COLOR_OVERRIDES_BIOME_GRASS.forEach((resourceKey, rgb) -> {
            Biome biome = world.getBiomeRegistry().get(resourceKey);
            grassColors.put(biome, rgb);
        });
        AdvancedConfig.COLOR_OVERRIDES_BIOME_FOLIAGE.forEach((resourceKey, rgb) -> {
            Biome biome = world.getBiomeRegistry().get(resourceKey);
            foliageColors.put(biome, rgb);
        });
        AdvancedConfig.COLOR_OVERRIDES_BIOME_WATER.forEach((resourceKey, rgb) -> {
            Biome biome = world.getBiomeRegistry().get(resourceKey);
            waterColors.put(biome, rgb);
        });
    }

    private static int[] getColorsFromImage(BufferedImage image) {
        int[] map = new int[256 * 256];
        for (int x = 0; x < 256; ++x) {
            for (int y = 0; y < 256; ++y) {
                int color = image.getRGB(x, y);
                int r = color >> 16 & 0xFF;
                int g = color >> 8 & 0xFF;
                int b = color & 0xFF;
                map[x + y * 256] = (r << 16) | (g << 8) | b;
            }
        }
        return map;
    }

    public boolean isGrassBlock(BlockState state) {
        return grassColorBlocks.contains(state.getBlock());
    }

    public boolean isFoliageBlock(BlockState state) {
        return foliageColorBlocks.contains(state.getBlock());
    }

    public boolean isWaterBlock(BlockState state) {
        return waterColorBlocks.contains(state.getBlock()) || waterColorMaterials.contains(state.getMaterial());
    }

    private int getDefaultGrassColor(double temperature, double humidity) {
        int j = (int) ((1.0 - (humidity * temperature)) * 255.0);
        int i = (int) ((1.0 - temperature) * 255.0);
        int k = j << 8 | i;
        if (k > mapGrass.length) {
            return 0;
        }
        return mapGrass[k];
    }

    private int getDefaultFoliageColor(double temperature, double humidity) {
        int i = (int) ((1.0 - temperature) * 255.0);
        int j = (int) ((1.0 - (humidity * temperature)) * 255.0);
        return mapFoliage[(j << 8 | i)];
    }

    private int grassColorSampler(ScanData data) {
        return data.getBlockBiome().getSpecialEffects().getGrassColorModifier().modifyColor(data.getCoordinate().getBlockX(), data.getCoordinate().getBlockZ(), this.grassColors.get(data.getBlockBiome()));
    }

    public int getGrassColor(ScanData data, ScanData.Data scanData) {
        if (this.world.getConfig().RENDER_BIOME_BLEND > 0) {
            return sampleNeighbors(scanData, data.getCoordinate(), this.world.getConfig().RENDER_BIOME_BLEND, this::grassColorSampler);
        }
        return grassColorSampler(data);
    }

    public int getFoliageColor(ScanData data, ScanData.Data scanData) {
        if (this.world.getConfig().RENDER_BIOME_BLEND > 0) {
            return sampleNeighbors(scanData, data.getCoordinate(), this.world.getConfig().RENDER_BIOME_BLEND, (data1) -> this.foliageColors.get(data1.getBlockBiome()));
        }
        return this.foliageColors.get(data.getBlockBiome());
    }

    public int getWaterColor(ScanData data, ScanData.Data scanData) {
        return getWaterColor(data, scanData, true);
    }

    public int getWaterColor(ScanData data, ScanData.Data scanData, boolean blend) {
        if (blend && this.world.getConfig().RENDER_BIOME_BLEND > 0) {
            return this.sampleNeighbors(scanData, data.getCoordinate(), this.world.getConfig().RENDER_BIOME_BLEND, (data1) -> this.waterColors.get(data1.getFluidBiome() == null ? data1.getBlockBiome() : data1.getFluidBiome()));
        }
        return this.waterColors.get(data.getFluidBiome());
    }

    private int sampleNeighbors(ScanData.Data scanData, BlockCoordinate coordinate, int radius, Function<ScanData, Integer> colorSampler) {
        List<Integer> colors = new ArrayList<>();
        for (int x = coordinate.getBlockX() - radius; x < coordinate.getBlockX() + radius; x++) {
            for (int z = coordinate.getBlockZ() - radius; z < coordinate.getBlockZ() + radius; z++) {
                ScanData data = scanData.get(new BlockCoordinate(x, z));
                if (data != null) {
                    colors.add(colorSampler.apply(data));
                } else {
                    // missing data?!
                    colors.add(0xFF0000);
                }
            }
        }
        return Colors.stack(colors);
    }
}
