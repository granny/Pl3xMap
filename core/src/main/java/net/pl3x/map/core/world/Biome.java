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
package net.pl3x.map.core.world;

import java.util.Objects;
import net.pl3x.map.core.Keyed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class Biome extends Keyed {
    public static final Biome DEFAULT = new Biome(0, "minecraft:default", 0x000070, 0x73A74E, 0x8EB971, 0x3F76E4, (x, z, def) -> def);
    private final int index;
    private final int color;
    private final int foliage;
    private final int grass;
    private final int water;
    private final @NonNull GrassModifier grassModifier;

    public Biome(int index, @NonNull String key, int color, int foliage, int grass, int water, @NonNull GrassModifier grassModifier) {
        super(key);
        this.index = index;
        this.color = color;
        this.foliage = foliage;
        this.grass = grass;
        this.water = water;
        this.grassModifier = grassModifier;
    }

    public int grass(int x, int z) {
        return grassModifier().modify(x, z, grass());
    }

    public int index() {
        return index;
    }

    public int color() {
        return color;
    }

    public int foliage() {
        return foliage;
    }

    public int grass() {
        return grass;
    }

    public int water() {
        return water;
    }

    public @NonNull GrassModifier grassModifier() {
        return grassModifier;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Biome other = (Biome) obj;
        return this.index == other.index &&
                getKey().equals(other.getKey()) &&
                this.color == other.color &&
                this.foliage == other.foliage &&
                this.grass == other.grass &&
                this.water == other.water &&
                Objects.equals(this.grassModifier, other.grassModifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(index, getKey(), color, foliage, grass, water, grassModifier);
    }

    @Override
    public @NonNull String toString() {
        return "Biome[" +
                "index=" + index + ", " +
                "key=" + getKey() + ", " +
                "color=" + color + ", " +
                "foliage=" + foliage + ", " +
                "grass=" + grass + ", " +
                "water=" + water + ", " +
                "grassModifier=" + grassModifier + ']';
    }


    @FunctionalInterface
    public interface GrassModifier {
        int modify(int x, int z, int def);
    }
}
