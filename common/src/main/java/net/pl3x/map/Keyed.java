package net.pl3x.map;

import org.jetbrains.annotations.NotNull;

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
}
