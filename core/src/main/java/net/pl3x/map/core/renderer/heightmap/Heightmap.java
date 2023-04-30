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
package net.pl3x.map.core.renderer.heightmap;

import java.util.Arrays;
import java.util.Objects;
import net.pl3x.map.core.Keyed;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Heightmap extends Keyed {
    public final int[] x = new int[16];
    public final int[] z = new int[16];

    public Heightmap(@NonNull String name) {
        super(name);
    }

    public abstract int getColor(@NonNull Region region, int blockX, int blockZ);

    public int getColor(int y1, int y2, int heightColor, int step) {
        if (y1 > y2) {
            heightColor -= step;
        } else if (y1 < y2) {
            heightColor += step;
        }
        return Mathf.clamp(0x00, 0x44, heightColor);
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Heightmap other = (Heightmap) o;
        return getKey().equals(other.getKey())
                && Arrays.equals(this.x, other.x)
                && Arrays.equals(this.z, other.z);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), Arrays.hashCode(this.x), Arrays.hashCode(this.z));
    }

    @Override
    public @NonNull String toString() {
        return "Heightmap{"
                + "key=" + getKey()
                + ",x=" + Arrays.toString(this.x)
                + ",z=" + Arrays.toString(this.z)
                + "}";
    }
}
