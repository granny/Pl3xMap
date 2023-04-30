/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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

@SuppressWarnings("UnusedReturnValue")
public class IconRegistry extends Registry<@NonNull IconImage> {
    private Path registeredDir;

    /**
     * Create a new image registry.
     */
    public void init() {
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
    public @NonNull Path getDir() {
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
    public @Nullable IconImage register(@Nullable IconImage image) {
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
     * @throws IllegalStateException if image failed to save to disk
     */
    @Override
    public @NonNull IconImage register(@NonNull String id, @NonNull IconImage image) {
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
    public @Nullable IconImage unregister(@NonNull String id) {
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
