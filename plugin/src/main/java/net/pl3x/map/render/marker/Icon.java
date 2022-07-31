package net.pl3x.map.render.marker;

import com.google.common.base.Preconditions;
import java.util.Objects;
import net.pl3x.map.render.marker.data.Key;
import net.pl3x.map.render.marker.data.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Icon marker.
 */
public class Icon extends Marker {
    private Key image;
    private Point location;
    private Point offset;
    private int width;
    private int height;

    public Icon() {
        this(Key.NONE, Point.ZERO, Point.ZERO, 0, 0);
    }

    public Icon(@NotNull Key image, @NotNull Point location, @NotNull Point offset, int width, int height) {
        setImage(image);
        setLocation(location);
        setOffset(offset);
        setWidth(width);
        setHeight(height);
    }

    /**
     * Get the image to use for this icon.
     *
     * @return image key
     * @see net.pl3x.map.render.marker.registry.IconRegistry
     */
    @NotNull
    public Key getImage() {
        return this.image;
    }

    /**
     * Set the image to use for this icon.
     * <p>
     * Must be registered with the icon registry.
     *
     * @param image new image
     * @return this icon
     * @see net.pl3x.map.render.marker.registry.IconRegistry
     */
    @NotNull
    public Icon setImage(@NotNull Key image) {
        Preconditions.checkNotNull(image);
        this.image = image;
        return this;
    }

    /**
     * Get the location where this icon will be located at on the map.
     *
     * @return map location
     */
    @NotNull
    public Point getLocation() {
        return this.location;
    }

    /**
     * Set a new map location for this icon.
     *
     * @param location new point
     * @return this icon
     */
    @NotNull
    public Icon setLocation(@NotNull Point location) {
        Preconditions.checkNotNull(location);
        this.location = location;
        return this;
    }

    /**
     * Get the offset of this icon from the map location.
     * <p>
     * The icon will be centered on the map location by default if size is specified.
     *
     * @return icon offset
     */
    @NotNull
    public Point getOffset() {
        return this.offset;
    }

    /**
     * Set the offset of this icon from the map location.
     * <p>
     * The icon will be centered on the map location by default if size is specified.
     *
     * @param offset new offset
     * @return this icon
     */
    @NotNull
    public Icon setOffset(@NotNull Point offset) {
        Preconditions.checkNotNull(offset);
        this.offset = offset;
        return this;
    }

    /**
     * Get the width of this icon.
     *
     * @return icon width
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * Set the icon width.
     *
     * @param width icon width
     * @return this icon
     */
    @NotNull
    public Icon setWidth(int width) {
        this.width = width;
        return this;
    }

    /**
     * Get the height of this icon.
     *
     * @return icon height
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * Set the icon height.
     *
     * @param height icon height
     * @return this icon
     */
    @NotNull
    public Icon setHeight(int height) {
        this.height = height;
        return this;
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
        Icon other = (Icon) o;
        return getImage().equals(other.getImage())
                && getLocation().equals(other.getLocation())
                && Objects.equals(getOffset(), other.getOffset())
                && getWidth() == other.getWidth()
                && getHeight() == other.getHeight()
                && getOptions().equals(other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getImage(), getLocation(), getOffset(), getWidth(), getHeight());
    }
}
