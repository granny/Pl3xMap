package net.pl3x.map.core.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class Registry<T> implements Iterable<T> {
    protected final Map<String, T> entries = new ConcurrentHashMap<>();

    public T register(String id, T value) {
        if (id == null) {
            throw new NullPointerException("Id cannot be null");
        }
        if (value == null) {
            throw new NullPointerException("Value cannot be null");
        }
        this.entries.put(id, value);
        return value;
    }

    public T unregister(String id) {
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

    public T get(String id) {
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
