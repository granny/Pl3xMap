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
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public class Registry<T extends Keyed> implements Iterable<T> {
    protected final Map<String, T> entries = new ConcurrentHashMap<>();

    public T register(T value) {
        return register(value.getKey(), value);
    }

    public T register(String id, T value) {
        Preconditions.checkNotNull(id, "Id cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        this.entries.put(id, value);
        return value;
    }

    public @Nullable T unregister(String id) {
        return this.entries.remove(id);
    }

    /**
     * Unregister all entries.
     */
    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    public boolean has(String key) {
        return this.entries.containsKey(key);
    }

    public @Nullable T get(String id) {
        return this.entries.get(id);
    }

    public T getOrDefault(String id, T def) {
        return this.entries.getOrDefault(id, def);
    }

    public Set<Map.Entry<String, T>> entrySet() {
        return this.entries.entrySet();
    }

    public Collection<T> values() {
        return this.entries.values();
    }

    public int size() {
        return this.entries.size();
    }

    @Override
    public Iterator<T> iterator() {
        return this.entries.values().iterator();
    }
}
