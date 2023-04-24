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

public class Registry<@NonNull T> implements Iterable<@NonNull T> {
    protected final Map<@NonNull String, @NonNull T> entries = new ConcurrentHashMap<>();

    public @NonNull T register(@NonNull String id, @NonNull T value) {
        Preconditions.checkNotNull(id, "Id cannot be null");
        Preconditions.checkNotNull(value, "Value cannot be null");
        this.entries.put(id, value);
        return value;
    }

    public @Nullable T unregister(@NonNull String id) {
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

    public @Nullable T get(@NonNull String id) {
        return this.entries.get(id);
    }

    public @NonNull T getOrDefault(@NonNull String id, @NonNull T def) {
        return this.entries.getOrDefault(id, def);
    }

    public @NonNull Set<Map.@NonNull Entry<@NonNull String, @NonNull T>> entrySet() {
        return this.entries.entrySet();
    }

    public @NonNull Collection<@NonNull T> values() {
        return this.entries.values();
    }

    public int size() {
        return this.entries.size();
    }

    @Override
    public @NonNull Iterator<@NonNull T> iterator() {
        return this.entries.values().iterator();
    }
}
