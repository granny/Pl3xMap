package net.pl3x.map.util;

import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.level.biome.Biome;
import net.pl3x.map.world.MapWorld;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class BiomeColors {
    private static final int[] mapGrass;
    private static final int[] mapFoliage;

    static {
        Path imagesDir = MapWorld.WEB_DIR.resolve("images");
        BufferedImage imgGrass, imgFoliage;

        try {
            imgGrass = ImageIO.read(imagesDir.resolve("grass.png").toFile());
            imgFoliage = ImageIO.read(imagesDir.resolve("foliage.png").toFile());
        } catch (final IOException e) {
            throw new IllegalStateException("Failed to read biome images", e);
        }

        mapGrass = getColorsFromImage(imgGrass);
        mapFoliage = getColorsFromImage(imgFoliage);
    }

    private final Map<Biome, Integer> grassColors = new HashMap<>();
    private final Map<Biome, Integer> foliageColors = new HashMap<>();
    private final Map<Biome, Integer> waterColors = new HashMap<>();

    public BiomeColors(ServerLevel level) {
        Registry<Biome> biomeRegistry = getBiomeRegistry(level);
        for (Biome biome : biomeRegistry) {
            float temperature = Mth.clamp(biome.getBaseTemperature(), 0.0F, 1.0F);
            float humidity = Mth.clamp(biome.getDownfall(), 0.0F, 1.0F);
            grassColors.put(biome, getDefaultGrassColor(temperature, humidity));
            foliageColors.put(biome, getDefaultFoliageColor(temperature, humidity));
            waterColors.put(biome, biome.getSpecialEffects().getWaterColor());
        }
    }

    public int getGrassColor(Biome biome) {
        return this.grassColors.get(biome);
    }

    public int getFoliageColor(Biome biome) {
        return this.foliageColors.get(biome);
    }

    public int getWaterColor(Biome biome) {
        return this.waterColors.get(biome);
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

    private static int getDefaultGrassColor(double temperature, double humidity) {
        int j = (int) ((1.0 - (humidity * temperature)) * 255.0);
        int i = (int) ((1.0 - temperature) * 255.0);
        int k = j << 8 | i;
        if (k > mapGrass.length) {
            return 0;
        }
        return mapGrass[k];
    }

    private static int getDefaultFoliageColor(double temperature, double humidity) {
        int i = (int) ((1.0 - temperature) * 255.0);
        int j = (int) ((1.0 - (humidity * temperature)) * 255.0);
        return mapFoliage[(j << 8 | i)];
    }
}
