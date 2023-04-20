package net.pl3x.map.core.registry;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Registry<T> implements Iterable<T> {
    protected final Map<String, T> entries = new ConcurrentHashMap<>();

    @NonNull
    public T register(@NonNull String id, @NonNull T value) {
        Preconditions.checkNotNull(id, "Id cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        this.entries.put(id, value);
        return value;
    }

    @Nullable
    public T unregister(@NonNull String id) {
        return this.entries.remove(id);
    }

    /**
     * Unregister all entries.
     */
    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    public boolean has(@NonNull String key) {
        return this.entries.containsKey(key);
    }

    @Nullable
    public T get(@NonNull String id) {
        return this.entries.get(id);
    }

    @NonNull
    public T getOrDefault(@NonNull String id, @NonNull T def) {
        return this.entries.getOrDefault(id, def);
    }

    @NonNull
    public Set<Map.Entry<String, T>> entrySet() {
        return this.entries.entrySet();
    }

    @NonNull
    public Collection<T> values() {
        return this.entries.values();
    }

    public int size() {
        return this.entries.size();
    }

    @Override
    @NonNull
    public Iterator<T> iterator() {
        return this.entries.values().iterator();
    }
}
