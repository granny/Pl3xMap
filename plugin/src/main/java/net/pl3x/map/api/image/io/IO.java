package net.pl3x.map.api.image.io;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.pl3x.map.api.image.Image;

public abstract class IO {
    private static final Map<String, Type> TYPES = new HashMap<>();

    public static void register(String name, Type type) {
        if (TYPES.containsKey(name)) {
            throw new IllegalStateException(String.format("IO type %s already registered", name));
        }
        TYPES.put(name, type);
    }

    public static void unregister(String name) {
        TYPES.remove(name);
    }

    public static IO.Type get(String format) {
        Type type = TYPES.get(format.toLowerCase(Locale.ROOT));
        if (type == null) {
            throw new IllegalStateException("Unknown or unsupported image format");
        }
        return type;
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
