package net.pl3x.map.util;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.StemBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MaterialColor;
import net.pl3x.map.configuration.Advanced;

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

    public static int mix(int color0, int color1, float ratio) {
        if (ratio >= 1F) return color1;
        else if (ratio <= 0F) return color0;
        float iRatio = 1.0F - ratio;
        int r = (int) ((red(color0) * iRatio) + (red(color1) * ratio));
        int g = (int) ((green(color0) * iRatio) + (green(color1) * ratio));
        int b = (int) ((blue(color0) * iRatio) + (blue(color1) * ratio));
        return setAlpha(0xFF, rgb(r, g, b));
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

    public static int getBlockColor(BlockState state) {
        Block block = state.getBlock();
        int color = Advanced.BLOCK_COLORS.getOrDefault(block, -1);

        if (color != 0) {
            if (block == Blocks.MELON_STEM || block == Blocks.PUMPKIN_STEM) {
                int age = state.getValue(StemBlock.AGE);
                color = rgb(age * 32, 0xFF - age * 8, age * 4);
            } else if (block == Blocks.WHEAT) {
                color = Colors.lerpRGB(MaterialColor.PLANT.col, color, (state.getValue(CropBlock.AGE) + 1) / 8F);
            } else if (block == Blocks.REDSTONE_WIRE) {
                color = RedStoneWireBlock.getColorForPower(state.getValue(RedStoneWireBlock.POWER));
            }
        }

        if (color < 0) {
            //noinspection ConstantConditions
            return state.getMapColor(null, null).col;
        }

        return color;
    }
}
