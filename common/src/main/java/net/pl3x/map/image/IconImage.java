package net.pl3x.map.image;

import java.awt.image.BufferedImage;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * Represents an icon image
 */
public class IconImage extends Keyed {
    private final BufferedImage image;

    /**
     * Create a new icon image.
     *
     * @param key   image key
     * @param image buffered image
     */
    public IconImage(@NotNull Key key, @NotNull BufferedImage image) {
        super(key);
        this.image = image;
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
}
