package net.pl3x.map.util.io;

import net.pl3x.map.render.Image;

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class IO {
    private static final Png PNG = new Png();
    private static final Webp WEBP = new Webp();

    public static Type get(String format) {
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

        protected abstract BufferedImage readNoLock(Path path);

        public final BufferedImage readBuffer(Path path) {
            //ReadWriteLock lock = IO.FILE_LOCKS.computeIfAbsent(path.toString(), k -> new ReentrantReadWriteLock(true));
            //lock.readLock().lock();
            BufferedImage buffer = readNoLock(path);
            //lock.readLock().unlock();
            return buffer;
        }

        protected abstract void writeNoLock(Path path, BufferedImage buffer);

        public final void writeBuffer(Path path, BufferedImage buffer) {
            //ReadWriteLock lock = IO.FILE_LOCKS.computeIfAbsent(path.toString(), k -> new ReentrantReadWriteLock(true));
            //lock.writeLock().lock();
            writeNoLock(path, buffer);
            //lock.writeLock().unlock();
        }
    }
}
