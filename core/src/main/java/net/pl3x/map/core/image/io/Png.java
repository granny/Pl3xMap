package net.pl3x.map.core.image.io;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Png extends IO.Type {
    @Override
    public @NonNull String extension() {
        return "png";
    }
}
