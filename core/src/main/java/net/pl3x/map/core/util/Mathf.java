/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
