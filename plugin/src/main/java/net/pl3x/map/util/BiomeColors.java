package net.pl3x.map.util;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import javax.imageio.ImageIO;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.pl3x.map.configuration.Advanced;
import net.pl3x.map.world.ChunkHelper;
import net.pl3x.map.world.MapWorld;

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
        Path imagesDir = MapWorld.WEB_DIR.resolve("images");
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

    private final MapWorld mapWorld;

    public BiomeColors(MapWorld mapWorld) {
        this.mapWorld = mapWorld;
        Registry<Biome> biomeRegistry = getBiomeRegistry(mapWorld.getLevel());
        for (Biome biome : biomeRegistry) {
            float temperature = Mth.clamp(biome.getBaseTemperature(), 0.0F, 1.0F);
            float humidity = Mth.clamp(biome.getDownfall(), 0.0F, 1.0F);
            grassColors.put(biome, getDefaultGrassColor(temperature, humidity));
            foliageColors.put(biome, getDefaultFoliageColor(temperature, humidity));
            waterColors.put(biome, biome.getSpecialEffects().getWaterColor());
        }

        Advanced.COLOR_OVERRIDES_BIOME_GRASS.forEach((resourceKey, rgb) -> {
            Biome biome = biomeRegistry.get(resourceKey);
            grassColors.put(biome, rgb);
        });
        Advanced.COLOR_OVERRIDES_BIOME_FOLIAGE.forEach((resourceKey, rgb) -> {
            Biome biome = biomeRegistry.get(resourceKey);
            foliageColors.put(biome, rgb);
        });
        Advanced.COLOR_OVERRIDES_BIOME_WATER.forEach((resourceKey, rgb) -> {
            Biome biome = biomeRegistry.get(resourceKey);
            waterColors.put(biome, rgb);
        });
    }

    public static Registry<Biome> getBiomeRegistry(ServerLevel world) {
        return world.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY);
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

    private int grassColorSampler(Biome biome, BlockPos pos) {
        return biome.getSpecialEffects().getGrassColorModifier().modifyColor(pos.getX(), pos.getZ(), this.grassColors.get(biome));
    }

    public int getGrassColor(ChunkHelper chunkHelper, Biome biome, BlockPos pos) {
        if (this.mapWorld.getConfig().RENDER_BIOME_BLEND > 0) {
            return sampleNeighbors(chunkHelper, pos, this.mapWorld.getConfig().RENDER_BIOME_BLEND, this::grassColorSampler);
        }
        return grassColorSampler(biome != null ? biome : chunkHelper.getBiomeWithCaching(this.mapWorld, pos).value(), pos);
    }

    public int getFoliageColor(ChunkHelper chunkHelper, Biome biome, BlockPos pos) {
        if (this.mapWorld.getConfig().RENDER_BIOME_BLEND > 0) {
            return sampleNeighbors(chunkHelper, pos, this.mapWorld.getConfig().RENDER_BIOME_BLEND, (biome1, pos1) -> this.foliageColors.get(biome1));
        }
        return this.foliageColors.get(biome != null ? biome : chunkHelper.getBiomeWithCaching(this.mapWorld, pos).value());
    }

    public int getWaterColor(ChunkHelper chunkHelper, Biome biome, BlockPos pos) {
        if (this.mapWorld.getConfig().RENDER_BIOME_BLEND > 0) {
            return this.sampleNeighbors(chunkHelper, pos, this.mapWorld.getConfig().RENDER_BIOME_BLEND, (biome1, pos1) -> this.waterColors.get(biome1));
        }
        return this.waterColors.get(biome != null ? biome : chunkHelper.getBiomeWithCaching(this.mapWorld, pos).value());
    }

    private int sampleNeighbors(ChunkHelper chunkHelper, BlockPos pos, int radius, BiFunction<Biome, BlockPos, Integer> colorSampler) {
        BlockPos.MutableBlockPos pos1 = new BlockPos.MutableBlockPos();
        int rgb, r = 0, g = 0, b = 0, count = 0;
        for (int x = pos.getX() - radius; x < pos.getX() + radius; x++) {
            for (int z = pos.getZ() - radius; z < pos.getZ() + radius; z++) {
                pos1.set(x, pos.getY(), z);
                Biome biome1 = chunkHelper.getBiomeWithCaching(this.mapWorld, pos1).value();
                rgb = colorSampler.apply(biome1, pos1);
                r += Colors.red(rgb);
                g += Colors.green(rgb);
                b += Colors.blue(rgb);
                count++;
            }
        }
        return Colors.rgb(r / count, g / count, b / count);
    }
}
