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
package net.pl3x.map.core.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.pl3x.map.core.Keyed;
import net.pl3x.map.core.util.Preconditions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Registry<T extends Keyed> implements Iterable<@NotNull T> {
    protected final Map<@NotNull String, @NotNull T> entries = new ConcurrentHashMap<>();

    public @NotNull T register(@NotNull T value) {
        return register(value.getKey(), value);
    }

    public @NotNull T register(@NotNull String id, @NotNull T value) {
        Preconditions.checkNotNull(id, "Id cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        this.entries.put(id, value);
        return value;
    }

    public @Nullable T unregister(@NotNull String id) {
        return this.entries.remove(id);
    }

    /**
     * Unregister all entries.
     */
    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    public boolean has(@NotNull String key) {
        return this.entries.containsKey(key);
    }

    public @Nullable T get(@NotNull String id) {
        return this.entries.get(id);
    }

    public @NotNull T getOrDefault(@NotNull String id, @NotNull T def) {
        return this.entries.getOrDefault(id, def);
    }

    public @NotNull Set<Map.@NotNull Entry<@NotNull String, @NotNull T>> entrySet() {
        return this.entries.entrySet();
    }

    public @NotNull Collection<@NotNull T> values() {
        return this.entries.values();
    }

    public int size() {
        return this.entries.size();
    }

    @Override
    public @NotNull Iterator<@NotNull T> iterator() {
        return this.entries.values().iterator();
    }
}
