package net.pl3x.map.core.image.io;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.registry.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class IO {
    private static final Registry<@NonNull Type> TYPES = new Registry<>();

    public static void register() {
        IO.register("bmp", new Bmp());
        IO.register("gif", new Gif());
        IO.register("jpg", new Jpg());
        IO.register("jpeg", get("jpg"));
        IO.register("png", new Png());
    }

    public static void register(@NonNull String name, @NonNull Type type) {
        if (TYPES.has(name)) {
            throw new IllegalStateException(String.format("IO type %s already registered", name));
        }
        TYPES.register(name, type);
    }

    public static void unregister() {
        TYPES.unregister();
    }

    public static void unregister(@NonNull String name) {
        TYPES.unregister(name);
    }

    public static @NonNull Type get(@NonNull String format) {
        Type type = TYPES.get(format.toLowerCase(Locale.ROOT));
        if (type == null) {
            throw new IllegalStateException("Unknown or unsupported image format");
        }
        return type;
    }

    public abstract static class Type {
        public abstract @NonNull String extension();

        public @NonNull BufferedImage createBuffer() {
            return new BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB);
        }

        public int color(int argb) {
            return argb;
        }

        public @Nullable BufferedImage read(@NonNull Path path) {
            BufferedImage buffer = null;
            ImageReader reader = null;
            try (ImageInputStream in = ImageIO.createImageInputStream(Files.newInputStream(path))) {
                reader = ImageIO.getImageReadersBySuffix(extension()).next();
                reader.setInput(in, false, true);
                buffer = reader.read(0);
                in.flush();
            } catch (IOException e) {
                Logger.warn("Could not read tile image: " + path);
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    reader.dispose();
                }
            }
            return buffer;
        }

        public void write(@NonNull Path path, @NonNull BufferedImage buffer) {
            ImageWriter writer = null;
            try (ImageOutputStream out = ImageIO.createImageOutputStream(path.toFile())) {
                writer = ImageIO.getImageWritersBySuffix(extension()).next();
                ImageWriteParam param = writer.getDefaultWriteParam();
                if (param.canWriteCompressed()) {
                    param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                    if (param.getCompressionType() == null) {
                        param.setCompressionType(param.getCompressionTypes()[0]);
                    }
                    param.setCompressionQuality((float) Config.WEB_TILE_QUALITY);
                }
                writer.setOutput(out);
                writer.write(null, new IIOImage(buffer, null, null), param);
                out.flush();
            } catch (IOException e) {
                Logger.warn("Could not write tile image: " + path);
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.dispose();
                }
            }
        }
    }
}
