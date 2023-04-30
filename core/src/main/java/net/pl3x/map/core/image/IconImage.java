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
package net.pl3x.map.core.image;

import java.awt.image.BufferedImage;
import java.util.Objects;
import net.pl3x.map.core.Keyed;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents an icon image
 */
public class IconImage extends Keyed {
    private final BufferedImage image;
    private final String type;

    /**
     * Create a new icon image.
     *
     * @param key   image key
     * @param image buffered image
     * @param type  image type
     */
    public IconImage(@NonNull String key, @NonNull BufferedImage image, @NonNull String type) {
        super(key);
        this.image = image;
        this.type = type;
    }

    /**
     * Get the image.
     *
     * @return buffered image
     */
    public @NonNull BufferedImage getImage() {
        return this.image;
    }

    /**
     * Get image type.
     *
     * @return image type
     */
    public @NonNull String getType() {
        return this.type;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        IconImage other = (IconImage) o;
        return getKey().equals(other.getKey())
                && getImage() == other.getImage()
                && getType().equals(other.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getImage(), getType());
    }

    @Override
    public @NonNull String toString() {
        return "IconImage{"
                + "key=" + getKey()
                + ",image=" + getImage()
                + ",type=" + getType()
                + "}";
    }
}
