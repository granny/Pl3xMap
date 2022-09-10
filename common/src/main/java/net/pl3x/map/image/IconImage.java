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
    public String getType() {
        return this.type;
    }
}
