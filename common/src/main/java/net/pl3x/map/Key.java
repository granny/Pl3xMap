package net.pl3x.map;

import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Simple string wrapper used to identify things.
 * <p>
 * In most cases keys should be unique, so prefixing keys with a plugin
 * name, for example {@code "myplugin_layer-1"}, would be good practice.
 */
public final class Key {
    private static final Pattern VALID_CHARS = Pattern.compile("^[a-zA-Z0-9.:/_-]+$");

    public static final Key NONE = new Key("none");

    private final String key;

    /**
     * Create a new key.
     *
     * @param key unique string
     */
    public Key(@NotNull String key) {
        if (!VALID_CHARS.matcher(key).matches()) {
            throw new IllegalArgumentException(String.format("Non [a-zA-Z0-9.:/_-] character in key '%s'", key));
        }
        this.key = key;
    }

    /**
     * Create a new key.
     *
     * @param key unique string
     * @return a new key
     */
    @NotNull
    public static Key of(@NotNull String key) {
        return new Key(key);
    }

    /**
     * Get the key string for this key.
     *
     * @return key string
     */
    @NotNull
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
        if (getClass() != o.getClass()) {
            return false;
        }
        Key other = (Key) o;
        return getKey().equals(other.getKey());
    }

    @Override
    public int hashCode() {
        return 31 + getKey().hashCode();
    }

    @Override
    public String toString() {
        return getKey();
    }
}
