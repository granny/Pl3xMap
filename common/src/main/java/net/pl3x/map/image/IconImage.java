package net.pl3x.map.image;

import java.awt.image.BufferedImage;
import java.util.Objects;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    public IconImage(@NotNull Key key, @NotNull BufferedImage image, String type) {
        super(key);
        this.image = image;
        this.type = type;
    }

    /**
     * Get the image.
     *
     * @return buffered image
     */
    @NotNull
    public BufferedImage getImage() {
        return this.image;
    }

    /**
     * Get image type.
     *
     * @return image type
     */
    @NotNull
    public String getType() {
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
        return getKey() == other.getKey()
                && getImage() == other.getImage()
                && getType().equals(other.getType());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getImage(), getType());
    }

    @Override
    public String toString() {
        return "IconImage{"
                + "key=" + getKey()
                + ",image=" + getImage()
                + ",type=" + getType()
                + "}";
    }
}
