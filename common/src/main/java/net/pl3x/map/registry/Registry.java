package net.pl3x.map.registry;

import java.util.Map;
import net.pl3x.map.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple registry interface.
 *
 * @param <T> registry type
 */
public interface Registry<T> {
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
    T register(@NotNull Key key, @NotNull T entry);

    /**
     * Unregister the entry for the provided key.
     * <p>
     * Will return null if no entry registered with provided key.
     *
     * @param key key
     * @return unregistered entry or null
     */
    @Nullable
    T unregister(@NotNull Key key);

    /**
     * Get the registered entry for the provided key.
     * <p>
     * Will return null if no entry registered with provided key.
     *
     * @param key key
     * @return registered entry or null
     */
    @Nullable
    T get(@NotNull Key key);

    /**
     * Get the registered entries
     *
     * @return map of registered entries
     */
    @NotNull
    Map<Key, T> entries();
}
