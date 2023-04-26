/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
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
package net.pl3x.map.core.markers.layer;

import java.util.function.Supplier;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a layer for worlds.
 */
@SuppressWarnings("UnusedReturnValue")
public abstract class WorldLayer extends SimpleLayer {
    private final World world;

    private Options options;

    /**
     * Create a new spawn layer.
     *
     * @param key           key for layer
     * @param world         world
     * @param labelSupplier label
     */
    public WorldLayer(@NonNull String key, @NonNull World world, @NonNull Supplier<@NonNull String> labelSupplier) {
        super(key, labelSupplier);
        this.world = world;
    }

    public @NonNull World getWorld() {
        return this.world;
    }

    public @Nullable Options getOptions() {
        return this.options;
    }

    public @NonNull WorldLayer setOptions(@Nullable Options options) {
        this.options = options;
        return this;
    }
}
