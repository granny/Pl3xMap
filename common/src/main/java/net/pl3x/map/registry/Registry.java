package net.pl3x.map.registry;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a simple registry of keyed objects.
 *
 * @param <T> registry type
 */
public class Registry<T extends Keyed> {
    protected final Map<Key, T> entries = new ConcurrentHashMap<>();

    /**
     * Register a new entry.
     * <p>
     * Will return null if an entry is already registered.
     *
     * @param entry entry to register
     * @return registered entry or null
     */
    @Nullable
    public T register(@NotNull T entry) {
        if (this.entries.containsKey(entry.getKey())) {
            return null;
        }
        this.entries.put(entry.getKey(), entry);
        return entry;
    }

    /**
     * Unregister the specified entry.
     * <p>
     * Will return null if entry is not registered.
     *
     * @param entry entry to unregister
     * @return unregistered entry or null
     */
    @Nullable
    public T unregister(@NotNull T entry) {
        return unregister(entry.getKey());
    }

    /**
     * Unregister the entry for the provided key.
     * <p>
     * Will return null if no entry registered with provided key.
     *
     * @param key key
     * @return unregistered entry or null
     */
    @Nullable
    public T unregister(@NotNull Key key) {
        return this.entries.remove(key);
    }

    /**
     * Unregister all entries.
     */
    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    /**
     * Get the registered entry for the provided key.
     * <p>
     * Will return null if no entry registered with provided key.
     *
     * @param key key
     * @return registered entry or null
     */
    @Nullable
    public T get(@NotNull Key key) {
        return this.entries.get(key);
    }

    /**
     * Get the registered entries
     *
     * @return map of registered entries
     */
    @NotNull
    public Map<Key, T> entries() {
        return Collections.unmodifiableMap(this.entries);
    }
}
