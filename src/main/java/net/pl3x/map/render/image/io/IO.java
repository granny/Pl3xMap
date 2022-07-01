package net.pl3x.map.render.image.io;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Locale;
import net.pl3x.map.render.image.Image;

public abstract class IO {
    private static final Png PNG = new Png();
    private static final Webp WEBP = new Webp();

    public static IO.Type get(String format) {
        return switch (format.toLowerCase(Locale.ROOT)) {
            case "png" -> PNG;
            case "webp" -> WEBP;
            default -> throw new IllegalArgumentException("Unknown or unsupported image format");
        };
    }

    public abstract static class Type {
        public BufferedImage createBuffer() {
            return new BufferedImage(Image.SIZE, Image.SIZE, BufferedImage.TYPE_INT_ARGB);
        }

        public abstract String extension();

        public abstract BufferedImage read(Path path);

        public abstract void write(Path path, BufferedImage buffer);
    }
}
