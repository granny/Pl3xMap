package net.pl3x.map.core.registry;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.pl3x.map.core.image.IconImage;
import net.pl3x.map.core.util.FileUtil;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class IconRegistry extends Registry<IconImage> {
    private final Path registeredDir;

    /**
     * Create a new image registry.
     */
    public IconRegistry() {
        this.registeredDir = FileUtil.getWebDir().resolve("images/icon/registered/");
        try {
            if (Files.exists(getDir())) {
                FileUtil.deleteDirectory(getDir());
            }
            Files.createDirectories(getDir());
        } catch (Throwable e) {
            throw new IllegalStateException("Failed to setup icon registry", e);
        }
    }

    /**
     * Get the directory where registered icon images are stored.
     *
     * @return icons directory
     */
    public Path getDir() {
        return this.registeredDir;
    }

    /**
     * Register a new image.
     * <p>
     * Will return null if the image is already registered.
     *
     * @param image image to register or null
     * @return registered image
     * @throws IllegalArgumentException if image is already registered
     * @throws IllegalStateException    if image failed to save to disk
     */
    @Nullable
    public IconImage register(@Nullable IconImage image) {
        if (image == null) {
            return null;
        }
        return register(image.getKey(), image);
    }

    /**
     * Register a new image.
     * <p>
     * Will return null if the image is already registered.
     *
     * @param image image to register or null
     * @return registered image
     * @throws IllegalArgumentException if image is already registered
     * @throws IllegalStateException    if image failed to save to disk
     */
    @Override
    @Nullable
    public IconImage register(String id, @Nullable IconImage image) {
        if (image == null) {
            return null;
        }
        if (this.entries.containsKey(id)) {
            return null;
        }
        try {
            String filename = id + "." + image.getType();
            File file = getDir().resolve(filename).toFile();
            ImageIO.write(image.getImage(), image.getType(), file);
        } catch (Throwable e) {
            throw new IllegalStateException(String.format("Failed to save image '%s'", id), e);
        }
        this.entries.put(id, image);
        return image;
    }

    /**
     * Unregister the image for the provided key.
     * <p>
     * Will return null if no image registered with provided key.
     *
     * @param id key
     * @return unregistered image or null
     * @throws IllegalStateException if image failed to delete from disk
     */
    @Override
    @Nullable
    public IconImage unregister(@NonNull String id) {
        IconImage image = super.unregister(id);
        if (image != null) {
            try {
                Files.delete(getDir().resolve(id + ".png"));
            } catch (NoSuchFileException ignore) {
            } catch (Throwable e) {
                throw new IllegalStateException(String.format("Failed to delete image for key '%s'", id), e);
            }
        }
        return image;
    }
}
