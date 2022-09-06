package net.pl3x.map.image;

import com.google.common.base.Preconditions;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.imageio.ImageIO;
import net.pl3x.map.Key;
import net.pl3x.map.registry.Registry;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The icon image registry.
 */
public class IconRegistry implements Registry<BufferedImage> {
    private final Map<Key, BufferedImage> images = new ConcurrentHashMap<>();
    private final Path dir;

    public IconRegistry() {
        this.dir = MapWorld.WEB_DIR.resolve("images/icon/registered/");
        try {
            if (Files.exists(getDir())) {
                FileUtil.deleteDirectory(getDir());
            }
            Files.createDirectories(getDir());
        } catch (IOException e) {
            throw new IllegalStateException("Failed to setup icon registry", e);
        }
    }

    /**
     * Get the directory where registered icon images are stored.
     *
     * @return icons directory
     */
    public Path getDir() {
        return this.dir;
    }

    @Override
    @Nullable
    public BufferedImage register(@NotNull Key key, @NotNull BufferedImage image) {
        Preconditions.checkArgument(get(key) == null, "Image already registered for key '%s'", key.getKey());
        try {
            ImageIO.write(image, "png", getDir().resolve(key.getKey() + ".png").toFile());
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to write image for key '%s'", key.getKey()), e);
        }
        this.images.put(key, image);
        return image;
    }

    @Override
    @Nullable
    public BufferedImage unregister(@NotNull Key key) {
        BufferedImage image = this.images.remove(key);
        if (image != null) {
            try {
                Files.delete(getDir().resolve(key.getKey() + ".png"));
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Failed to delete image for key '%s'", key.getKey()), e);
            }
        }
        return image;
    }

    @Override
    @Nullable
    public BufferedImage get(@NotNull Key key) {
        return this.images.get(key);
    }

    @Override
    @NotNull
    public Map<Key, BufferedImage> entries() {
        return Collections.unmodifiableMap(this.images);
    }
}
