package net.pl3x.map.core.image.io;

import java.awt.image.BufferedImage;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Jpg extends IO.Type {
    @Override
    @NonNull
    public String extension() {
        return "jpg";
    }

    @Override
    @NonNull
    public BufferedImage createBuffer() {
        return new BufferedImage(512, 512, BufferedImage.TYPE_INT_RGB);
    }

    @Override
    public int color(int argb) {
        return argb & 0xFFFFFF;
    }
}
