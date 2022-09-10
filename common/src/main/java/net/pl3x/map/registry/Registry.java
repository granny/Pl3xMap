package net.pl3x.map.registry;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a key-value pair registry.
 *
 * @param <K> key type
 * @param <V> value type
 */
public class Registry<K, V> {
    protected final Map<K, V> entries = new ConcurrentHashMap<>();

    /**
     * Register a new value with key.
     * <p>
     * Will return null if the key is already registered.
     *
     * @param key   key to register
     * @param value value to register
     * @return registered value or null
     */
    @Nullable
    public V register(@NotNull K key, @NotNull V value) {
        if (this.entries.containsKey(key)) {
            return null;
        }
        this.entries.put(key, value);
        return value;
    }

    /**
     * Unregister the value for the provided key.
     * <p>
     * Will return null if no value registered with provided key.
     *
     * @param key key
     * @return unregistered value or null
     */
    @Nullable
    public V unregister(@NotNull K key) {
        return this.entries.remove(key);
    }

    /**
     * Unregister all entries.
     */
    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    /**
     * Get the registered value for the provided key.
     * <p>
     * Will return null if no value registered with provided key.
     *
     * @param key key
     * @return registered value or null
     */
    @Nullable
    public V get(@NotNull K key) {
        return this.entries.get(key);
    }

    /**
     * Get the registered entries
     *
     * @return map of registered entries
     */
    @NotNull
    public Map<K, V> entries() {
        return Collections.unmodifiableMap(this.entries);
    }

    /**
     * Get the number of registered entries.
     *
     * @return number of entries
     */
    public int size() {
        return this.entries.size();
    }
}
