package net.pl3x.map;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a key identified object.
 */
public abstract class Keyed {
    private final Key key;

    /**
     * Create a new key identified object.
     *
     * @param key key for object
     */
    public Keyed(@NotNull Key key) {
        this.key = key;
    }

    /**
     * Get the identifying key.
     *
     * @return the key
     */
    @NotNull
    public Key getKey() {
        return this.key;
    }

    @Override
    public abstract boolean equals(@Nullable Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract String toString();
}
