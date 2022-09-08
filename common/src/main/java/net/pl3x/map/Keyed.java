package net.pl3x.map;

import org.jetbrains.annotations.NotNull;

public abstract class Keyed {
    private final Key key;

    public Keyed(Key key) {
        this.key = key;
    }

    @NotNull
    public Key getKey() {
        return this.key;
    }
}
