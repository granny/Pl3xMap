package net.pl3x.map.core.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.BlockState;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;

public class Colors {
    private static final int[] mapGrass;
    private static final int[] mapFoliage;

    static {
        Path imagesDir = FileUtil.getWebDir().resolve("images");
        BufferedImage imgGrass, imgFoliage;

        try {
            imgGrass = ImageIO.read(imagesDir.resolve("grass.png").toFile());
            imgFoliage = ImageIO.read(imagesDir.resolve("foliage.png").toFile());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to read color images", e);
        }

        mapGrass = getColorsFromImage(imgGrass);
        mapFoliage = getColorsFromImage(imgFoliage);
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

    public static int getDefaultGrassColor(double temperature, double humidity) {
        return getDefaultColor(temperature, humidity, mapGrass);
    }

    public static int getDefaultFoliageColor(double temperature, double humidity) {
        return getDefaultColor(temperature, humidity, mapFoliage);
    }

    private static int getDefaultColor(double temperature, double humidity, int[] map) {
        int i = (int) ((1.0 - temperature) * 255.0);
        int j = (int) ((1.0 - (humidity * temperature)) * 255.0);
        int k = j << 8 | i;
        return k > map.length ? 0 : map[k];
    }

    public static int lerpARGB(int color0, int color1, float delta) {
        if (color0 == color1) return color0;
        if (delta >= 1F) return color1;
        if (delta <= 0F) return color0;
        return ((int) Mathf.lerp(color0 >> 24 & 0xFF, color1 >> 24 & 0xFF, delta)) << 24 | ((int) Mathf.lerp(color0 >> 16 & 0xFF, color1 >> 16 & 0xFF, delta)) << 16 | ((int) Mathf.lerp(color0 >> 8 & 0xFF, color1 >> 8 & 0xFF, delta)) << 8 | ((int) Mathf.lerp(color0 & 0xFF, color1 & 0xFF, delta));
    }

    /**
     * Blends one color over another.
     *
     * @param color0 color to blend over with
     * @param color1 color to be blended over
     * @return resulting blended color
     * @see <a href="https://en.wikipedia.org/wiki/Alpha_compositing#Alpha_blending">Alpha Blending</a>
     */
    public static int blend(int color0, int color1) {
        double a0 = (double) (color0 >> 24 & 0xFF) / 0xFF;
        double a1 = (double) (color1 >> 24 & 0xFF) / 0xFF;
        double a = a0 + a1 * (1 - a0);
        double r = ((color0 >> 16 & 0xFF) * a0 + (color1 >> 16 & 0xFF) * a1 * (1 - a0)) / a;
        double g = ((color0 >> 8 & 0xFF) * a0 + (color1 >> 8 & 0xFF) * a1 * (1 - a0)) / a;
        double b = ((color0 & 0xFF) * a0 + (color1 & 0xFF) * a1 * (1 - a0)) / a;
        return ((int) a * 0xFF) << 24 | ((int) r) << 16 | ((int) g) << 8 | ((int) b);
    }

    public static int mix(int color0, int color1) {
        int r = (color0 >> 16 & 0xFF) + (color1 >> 16 & 0xFF);
        int g = (color0 >> 8 & 0xFF) + (color1 >> 8 & 0xFF);
        int b = (color0 & 0xFF) + (color1 & 0xFF);
        return (r >> 1) << 16 | (g >> 1) << 8 | (b >> 1);
    }

    public static int getFoliageColor(Region region, Biome biome, int color, int x, int z) {
        return sampleNeighbors(region, biome, x, z, (biome2, x2, z2) -> mix(biome2.foliage(), color));
    }

    public static int getGrassColor(Region region, Biome biome, int color, int x, int z) {
        return sampleNeighbors(region, biome, x, z, (biome2, x2, z2) -> mix(biome2.grass(x2, z2), color));
    }

    public static int getWaterColor(Region region, Biome biome, int x, int z) {
        return sampleNeighbors(region, biome, x, z, (biome2, x2, z2) -> biome2.water());
    }

    private static int sampleNeighbors(Region region, Biome biome, int x, int z, TriFunction<Biome, Integer, Integer, Integer> colorSampler) {
        int radius = region.getWorld().getConfig().RENDER_BIOME_BLEND;
        int color = colorSampler.apply(biome, x, z);
        if (radius < 1) {
            return color;
        }
        int red = color >> 16 & 0xFF;
        int green = color >> 8 & 0xFF;
        int blue = color & 0xFF;
        int count = 1;
        for (int x2 = x - radius; x2 < x + radius; x2++) {
            for (int z2 = z - radius; z2 < z + radius; z2++) {
                if (x2 == x && z2 == z) {
                    continue;
                }
                Chunk.BlockData data = region.getWorld().getChunk(region, x2 >> 4, z2 >> 4).getData(x2, z2); // 3%
                if (data == null) {
                    continue;
                }
                int color2 = colorSampler.apply(data.getBiome(region, x2, z2), x2, z2); // 2%
                if (color2 > 0) {
                    red += color2 >> 16 & 0xFF;
                    green += color2 >> 8 & 0xFF;
                    blue += color2 & 0xFF;
                    count++;
                }
            }
        }
        return (red / count) << 16 | (green / count) << 8 | (blue / count);
    }

    public static int fixBlockColor(Region region, Biome biome, BlockState blockstate, int x, int z) {
        int color = blockstate.getBlock().color();
        if (color <= 0) {
            return 0;
        }
        if (blockstate.getBlock().isFoliage()) {
            return getFoliageColor(region, biome, color, x, z);
        }
        if (blockstate.getBlock().isGrass()) {
            return getGrassColor(region, biome, color, x, z);
        }
        if (blockstate.getBlock().isWater()) {
            return getWaterColor(region, biome, x, z);
        }
        /*
        if (state.is(Blocks.MELON_STEM) || state.is(Blocks.PUMPKIN_STEM)) {
            int age = state.getValue(StemBlock.AGE);
            return Colors.rgb(age * 32, 0xFF - age * 8, age * 4);
        }
        if (state.is(Blocks.WHEAT)) {
            return Colors.lerpRGB(MaterialColor.PLANT.col, color, (state.getValue(CropBlock.AGE) + 1) / 8F);
        }
        if (state.is(Blocks.REDSTONE_WIRE)) {
            return RedStoneWireBlock.getColorForPower(state.getValue(RedStoneWireBlock.POWER));
        }
        if (state.is(Blocks.COCOA)) {
            return switch (state.getValue(CocoaBlock.AGE)) {
                case 0 -> 0x6A682E;
                case 1 -> 0x654721;
                default -> 0x703715;
            };
        }
        if (state.is(Blocks.FARMLAND)) {
            return state.getValue(FarmBlock.MOISTURE) >= 7 ? 0x512C0F : 0x8E6646;
        }
        */
        return blockstate.getBlock().color();
    }

    public static int fromHex(String color) {
        return (int) Long.parseLong(color.replace("#", ""), 16);
    }

    public static String toHex(int rgb) {
        return String.format("#%06X", (0xFFFFFF & rgb));
    }

    public static String toHex8(int rgb) {
        return String.format("#%08X", rgb);
    }
}
