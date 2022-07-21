package net.pl3x.map.render.marker;

import java.util.Objects;
import java.util.regex.Pattern;

public class Key {
    private static final Pattern VALID_CHARS = Pattern.compile("^[a-zA-Z0-9._-]$");

    private final String key;

    public Key(String key) {
        if (!VALID_CHARS.matcher(key).matches()) {
            throw new IllegalArgumentException(String.format("Non [a-zA-Z0-9._-] character in key '%s'", key));
        }
        this.key = key;
    }

    public String getKey() {
        return this.key;
    }

    @Override
    public boolean equals(Object o) {
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
        return 31 + Objects.hashCode(getKey());
    }

    @Override
    public String toString() {
        return getKey();
    }
}
