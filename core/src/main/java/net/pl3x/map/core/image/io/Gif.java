package net.pl3x.map.core.image.io;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Gif extends IO.Type {
    @Override
    @NonNull
    public String extension() {
        return "gif";
    }
}
