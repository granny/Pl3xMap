package net.pl3x.map.core.util;

import net.pl3x.map.core.markers.Point;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Mathf {
    public static float lerp(float a, float b, float t) {
        return a + t * (b - a);
    }

    public static float inverseLerp(float a, float b, float t) {
        return (t - a) / (b - a);
    }

    public static double clamp(double min, double max, double value) {
        return Math.min(Math.max(value, min), max);
    }

    public static float clamp(float min, float max, float value) {
        return Math.min(Math.max(value, min), max);
    }

    public static int clamp(int min, int max, int value) {
        return Math.min(Math.max(value, min), max);
    }

    public static int pow2(int value) {
        return 1 << value;
    }

    public static long asLong(@NonNull Point pos) {
        return asLong(pos.x(), pos.z());
    }

    public static long asLong(long x, long z) {
        return (x & 0xFFFFFFFFL) | (z & 0xFFFFFFFFL) << 32;
    }

    public static int longToX(long pos) {
        return (int) (pos & 0xFFFFFFFFL);
    }

    public static int longToZ(long pos) {
        return (int) (pos >>> 32 & 0xFFFFFFFFL);
    }
}
