package net.pl3x.map.util;

import java.awt.Color;
import java.util.List;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CocoaBlock;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.pl3x.map.configuration.AdvancedConfig;
import net.pl3x.map.render.ScanData;

public class Colors {
    private Colors() {
    }

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

    public static int lerpHSB(int color0, int color1, float delta) {
        return lerpHSB(color0, color1, delta, true);
    }

    public static int lerpHSB(int color0, int color1, float delta, boolean useShortestAngle) {
        float[] hsb0 = Color.RGBtoHSB(red(color0), green(color0), blue(color0), null);
        float[] hsb1 = Color.RGBtoHSB(red(color1), green(color1), blue(color1), null);
        return setAlpha(
                (int) Mathf.lerp(alpha(color0), alpha(color1), delta),
                Color.HSBtoRGB(
                        useShortestAngle ?
                                lerpShortestAngle(hsb0[0], hsb1[0], delta) :
                                Mathf.lerp(hsb0[0], hsb1[0], delta),
                        Mathf.lerp(hsb0[1], hsb1[1], delta),
                        Mathf.lerp(hsb0[2], hsb1[2], delta)
                )
        );
    }

    public static int inverseLerpRGB(int color0, int color1, float delta) {
        if (color0 == color1) return color0;
        if (delta >= 1F) return color1;
        if (delta <= 0F) return color0;
        return rgb(
                (int) Mathf.inverseLerp(red(color0), red(color1), delta),
                (int) Mathf.inverseLerp(green(color0), green(color1), delta),
                (int) Mathf.inverseLerp(blue(color0), blue(color1), delta)
        );
    }

    public static int inverseLerpARGB(int color0, int color1, float delta) {
        if (color0 == color1) return color0;
        if (delta >= 1F) return color1;
        if (delta <= 0F) return color0;
        return argb(
                (int) Mathf.inverseLerp(alpha(color0), alpha(color1), delta),
                (int) Mathf.inverseLerp(red(color0), red(color1), delta),
                (int) Mathf.inverseLerp(green(color0), green(color1), delta),
                (int) Mathf.inverseLerp(blue(color0), blue(color1), delta)
        );
    }

    public static int inverseLerpHSB(int color0, int color1, float delta) {
        return inverseLerpHSB(color0, color1, delta, true);
    }

    public static int inverseLerpHSB(int color0, int color1, float delta, boolean useShortestAngle) {
        float[] hsb0 = Color.RGBtoHSB(red(color0), green(color0), blue(color0), null);
        float[] hsb1 = Color.RGBtoHSB(red(color1), green(color1), blue(color1), null);
        return setAlpha(
                (int) Mathf.inverseLerp(alpha(color0), alpha(color1), delta),
                Color.HSBtoRGB(
                        useShortestAngle ?
                                lerpShortestAngle(hsb0[0], hsb1[0], delta) :
                                Mathf.inverseLerp(hsb0[0], hsb1[0], delta),
                        Mathf.inverseLerp(hsb0[1], hsb1[1], delta),
                        Mathf.inverseLerp(hsb0[2], hsb1[2], delta)
                )
        );
    }

    public static float lerpShortestAngle(float start, float end, float delta) {
        float distCW = (end >= start ? end - start : 1F - (start - end));
        float distCCW = (start >= end ? start - end : 1F - (end - start));
        float direction = (distCW <= distCCW ? distCW : -1F * distCCW);
        return (start + (direction * delta));
    }

    public static int mix(int color0, int color1, float ratio) {
        if (ratio >= 1F) return color1;
        else if (ratio <= 0F) return color0;
        float iRatio = 1.0F - ratio;
        int r = (int) ((red(color0) * iRatio) + (red(color1) * ratio));
        int g = (int) ((green(color0) * iRatio) + (green(color1) * ratio));
        int b = (int) ((blue(color0) * iRatio) + (blue(color1) * ratio));
        return argb(0xFF, r, g, b);
    }

    public static int mix(int color0, int color1) {
        return mix(color0, color1, alpha(color1) / (float) 0xFF);
    }

    public static int stack(List<Integer> colors) {
        int r = 0, g = 0, b = 0, count = 0;
        for (int rgb : colors) {
            r += Colors.red(rgb);
            g += Colors.green(rgb);
            b += Colors.blue(rgb);
            count++;
        }
        return Colors.rgb(r / count, g / count, b / count);
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
        double a0 = (double) alpha(color0) / 0xFF;
        double a1 = (double) alpha(color1) / 0xFF;
        double a = a0 + a1 * (1 - a0);
        double r = (red(color0) * a0 + red(color1) * a1 * (1 - a0)) / a;
        double g = (green(color0) * a0 + green(color1) * a1 * (1 - a0)) / a;
        double b = (blue(color0) * a0 + blue(color1) * a1 * (1 - a0)) / a;
        return argb(
                (int) Math.round(a) * 0xFF,
                (int) Math.round(r),
                (int) Math.round(g),
                (int) Math.round(b)
        );
    }

    public static int rgb(int red, int green, int blue) {
        return red << 16 | green << 8 | blue;
    }

    public static int argb(int alpha, int red, int green, int blue) {
        return alpha << 24 | red << 16 | green << 8 | blue;
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

    public static int setAlpha(int alpha, int color) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    public static int fromHex(String color) {
        return (int) Long.parseLong(color.replace("#", ""), 16);
    }

    public static String toHex(int rgb) {
        return String.format("#%06X", (0xFFFFFF & rgb));
    }

    public static int getRawBlockColor(BlockState state) {
        int color = AdvancedConfig.BLOCK_COLORS.getOrDefault(state.getBlock(), -1);
        if (color < 0) {
            //noinspection ConstantConditions
            return state.getMapColor(null, null).col;
        }
        return color;
    }

    public static int fixBlockColor(BiomeColors biomeColors, ScanData data, ScanData.Data scanData, int color) {
        final BlockState state = data.getBlockState();
        if (biomeColors.isGrassBlock(state)) {
            return biomeColors.getGrassColor(data, scanData);
        }
        if (biomeColors.isFoliageBlock(state)) {
            return biomeColors.getFoliageColor(data, scanData);
        }
        if (biomeColors.isWaterBlock(state)) {
            return biomeColors.getWaterColor(data, scanData);
        }
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
        return color;
    }
}
