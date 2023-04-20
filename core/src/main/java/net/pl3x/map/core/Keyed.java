package net.pl3x.map.core;

import java.util.Objects;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a key identified object.
 */
public abstract class Keyed {
    private final String key;

    /**
     * Create a new key identified object.
     *
     * @param key key for object
     */
    public Keyed(@NonNull String key) {
        this.key = Preconditions.checkNotNull(key, "Key is null");
    }

    /**
     * Get the identifying key.
     *
     * @return the key
     */
    @NonNull
    public String getKey() {
        return this.key;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Keyed other = (Keyed) o;
        return getKey().equals(other.getKey());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey());
    }

    @Override
    @NonNull
    public String toString() {
        return this.key;
    }
}
