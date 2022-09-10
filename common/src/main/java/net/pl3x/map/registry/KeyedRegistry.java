package net.pl3x.map.registry;

import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a registry of keyed objects.
 *
 * @param <T> keyed type
 */
public class KeyedRegistry<T extends Keyed> extends Registry<Key, T> {
    /**
     * Register a new entry.
     * <p>
     * Will return null if the entry is already registered.
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
}
