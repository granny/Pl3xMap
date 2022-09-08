package net.pl3x.map.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.pl3x.map.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple registry interface.
 *
 * @param <T> registry type
 */
public abstract class Registry<T> {
    protected final Map<Key, T> entries = new ConcurrentHashMap<>();

    /**
     * Register a new entry with the provided key.
     * <p>
     * Will return null if an entry is already registered with provided key.
     *
     * @param key   key
     * @param entry entry to register
     * @return registered entry or null
     */
    @Nullable
    public abstract T register(@NotNull Key key, @NotNull T entry);

    /**
     * Unregister the entry for the provided key.
     * <p>
     * Will return null if no entry registered with provided key.
     *
     * @param key key
     * @return unregistered entry or null
     */
    @Nullable
    public abstract T unregister(@NotNull Key key);

    /**
     * Unregister all entries.
     */
    public abstract void unregister();

    /**
     * Get the registered entry for the provided key.
     * <p>
     * Will return null if no entry registered with provided key.
     *
     * @param key key
     * @return registered entry or null
     */
    @Nullable
    public abstract T get(@NotNull Key key);

    /**
     * Get the registered entries
     *
     * @return map of registered entries
     */
    @NotNull
    public abstract Map<Key, T> entries();
}
