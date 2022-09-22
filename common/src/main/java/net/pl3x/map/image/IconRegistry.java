package net.pl3x.map.image;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.imageio.ImageIO;
import net.pl3x.map.Key;
import net.pl3x.map.registry.KeyedRegistry;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The icon image registry.
 */
public class IconRegistry extends KeyedRegistry<IconImage> {
    private final Path registeredDir;

    /**
     * Create a new image registry.
     */
    public IconRegistry() {
        this.registeredDir = World.WEB_DIR.resolve("images/icon/registered/");
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
    @Override
    @Nullable
    public IconImage register(@Nullable IconImage image) {
        if (image == null) {
            return null;
        }
        if (this.entries.containsKey(image.getKey())) {
            return null;
        }
        try {
            String filename = image.getKey() + "." + image.getType();
            File file = getDir().resolve(filename).toFile();
            ImageIO.write(image.getImage(), image.getType(), file);
        } catch (Throwable e) {
            throw new IllegalStateException(String.format("Failed to save image '%s'", image.getKey()), e);
        }
        this.entries.put(image.getKey(), image);
        return image;
    }

    /**
     * Unregister the image for the provided key.
     * <p>
     * Will return null if no image registered with provided key.
     *
     * @param key key
     * @return unregistered image or null
     * @throws IllegalStateException if image failed to delete from disk
     */
    @Override
    @Nullable
    public IconImage unregister(@NotNull Key key) {
        IconImage image = super.unregister(key);
        if (image != null) {
            try {
                Files.delete(getDir().resolve(key + ".png"));
            } catch (Throwable e) {
                throw new IllegalStateException(String.format("Failed to delete image for key '%s'", key), e);
            }
        }
        return image;
    }
}
