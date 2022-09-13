package net.pl3x.map.util;

public class Mathf {
    private Mathf() {
    }

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
}
