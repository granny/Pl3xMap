package net.pl3x.map.markers.marker;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.JsonArrayWrapper;
import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.image.IconRegistry;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an icon marker.
 */
public class Icon extends Marker<Icon> {
    private Point point;
    private Key image;
    private Key retina;
    private Vector size;
    private Vector anchor;
    private Key shadow;
    private Key shadowRetina;
    private Vector shadowSize;
    private Vector shadowAnchor;

    private Icon() {
        super("icon");
    }

    /**
     * Create a new icon.
     *
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     */
    public Icon(double x, double z, @NotNull Key image) {
        this(Point.of(x, z), image);
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     */
    public Icon(@NotNull Point point, @NotNull Key image) {
        this(point, image, null);
    }

    /**
     * Create a new icon.
     *
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @param size  size of image
     */
    public Icon(double x, double z, @NotNull Key image, double size) {
        this(Point.of(x, z), image, Vector.of(size, size));
    }

    /**
     * Create a new icon.
     *
     * @param x      icon x location on map
     * @param z      icon z location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     */
    public Icon(double x, double z, @NotNull Key image, double width, double height) {
        this(Point.of(x, z), image, Vector.of(width, height));
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     */
    public Icon(@NotNull Point point, @NotNull Key image, double size) {
        this(point, image, Vector.of(size, size));
    }

    /**
     * Create a new icon.
     *
     * @param point  icon location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     */
    public Icon(@NotNull Point point, @NotNull Key image, double width, double height) {
        this(point, image, Vector.of(width, height));
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     */
    public Icon(@NotNull Point point, @NotNull Key image, @Nullable Vector size) {
        this();
        setPoint(point);
        setImage(image);
        setSize(size);
    }

    /**
     * Create a new icon.
     *
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @return a new icon
     */
    public static Icon of(double x, double z, @NotNull Key image) {
        return icon(Point.of(x, z), image);
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @return a new icon
     */
    public static Icon of(@NotNull Point point, @NotNull Key image) {
        return icon(point, image, null);
    }

    /**
     * Create a new icon.
     *
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon of(double x, double z, @NotNull Key image, double size) {
        return icon(Point.of(x, z), image, Vector.of(size, size));
    }

    /**
     * Create a new icon.
     *
     * @param x      icon x location on map
     * @param z      icon z location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    public static Icon of(double x, double z, @NotNull Key image, double width, double height) {
        return icon(Point.of(x, z), image, Vector.of(width, height));
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon of(@NotNull Point point, @NotNull Key image, double size) {
        return icon(point, image, Vector.of(size, size));
    }

    /**
     * Create a new icon.
     *
     * @param point  icon location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    public static Icon of(@NotNull Point point, @NotNull Key image, double width, double height) {
        return icon(point, image, Vector.of(width, height));
    }

    /**
     * Create a new icon.
     *
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    public static Icon of(@NotNull Point point, @NotNull Key image, @Nullable Vector size) {
        return new Icon(point, image).setSize(size);
    }

    /**
     * Get the {@link Point} on the map for this icon.
     *
     * @return point on map
     */
    @NotNull
    public Point getPoint() {
        return this.point;
    }

    /**
     * Set a new {@link Point} on the map for this icon.
     *
     * @param point new point on map
     * @return this icon
     */
    @NotNull
    public Icon setPoint(@NotNull Point point) {
        Preconditions.checkNotNull(point, "Icon point is null");
        this.point = point;
        return this;
    }

    /**
     * Get the image to use for this icon.
     *
     * @return image
     * @see IconRegistry
     */
    @NotNull
    public Key getImage() {
        return this.image;
    }

    /**
     * Set the image to use for this icon.
     * <p>
     * Key must be registered with the icon registry.
     *
     * @param image new image
     * @return this icon
     * @see IconRegistry
     */
    @NotNull
    public Icon setImage(@NotNull Key image) {
        Preconditions.checkNotNull(image, "Image key is null");
        Preconditions.checkNotNull(Pl3xMap.api().getIconRegistry().get(image), String.format("Icon not in registry (%s)", image));
        this.image = image;
        return this;
    }

    /**
     * Get the retina sized image to use for this icon.
     * <p>
     * This image will be used on retina devices.
     * <p>
     * Defaults to '<code>{@link #getImage()}</code>' if null.
     *
     * @return retina image
     * @see IconRegistry
     */
    @Nullable
    public Key getRetina() {
        return this.retina;
    }

    /**
     * Set the retina sized image to use for this icon.
     * <p>
     * This image will be used on retina devices.
     * <p>
     * Key must be registered with the icon registry.
     * <p>
     * Defaults to '<code>{@link #getImage()}</code>' if null.
     *
     * @param retina new retina image
     * @return this icon
     * @see IconRegistry
     */
    @NotNull
    public Icon setRetina(@Nullable Key retina) {
        Preconditions.checkArgument(retina == null || Pl3xMap.api().getIconRegistry().has(retina), String.format("Icon not in registry (%s)", retina));
        this.retina = retina;
        return this;
    }

    /**
     * Get the size of the image, in pixels.
     * <p>
     * Used for auto centering the image on '<code>{@link #getPoint()}</code>' if set.
     *
     * @return image size
     */
    @Nullable
    public Vector getSize() {
        return this.size;
    }

    /**
     * Set the size of the image, in pixels.
     * <p>
     * Used for auto centering the image on '<code>{@link #getPoint()}</code>' if set.
     *
     * @param size new image size
     * @return this icon
     */
    @NotNull
    public Icon setSize(@Nullable Vector size) {
        this.size = size;
        return this;
    }

    /**
     * Get the coordinates of the "tip" of the icon (relative to its top left corner).
     * <p>
     * The icon will be aligned so that this point is at {@link #getPoint()}.
     * <p>
     * Centered by default if '<code>{@link #getSize()}</code>' is also set.
     *
     * @return icon anchor
     */
    @Nullable
    public Vector getAnchor() {
        return this.anchor;
    }

    /**
     * Set the coordinates of the "tip" of the icon (relative to its top left corner).
     * <p>
     * The icon will be aligned so that this point is at {@link #getPoint()}.
     * <p>
     * Centered by default if '<code>{@link #getSize()}</code>' is also set.
     *
     * @param anchor new anchor
     * @return this icon
     */
    @NotNull
    public Icon setAnchor(@Nullable Vector anchor) {
        this.anchor = anchor;
        return this;
    }

    /**
     * Get shadow image of this icon.
     * <p>
     * No shadow image will be shown, if null.
     *
     * @return shadow image
     */
    @Nullable
    public Key getShadow() {
        return this.shadow;
    }

    /**
     * Set the shadow image to use for this icon.
     * <p>
     * Key must be registered with the icon registry.
     * <p>
     * No shadow image will be shown, if null.
     *
     * @param shadow new shadow image
     * @return this icon
     * @see IconRegistry
     */
    @NotNull
    public Icon setShadow(@Nullable Key shadow) {
        Preconditions.checkArgument(shadow == null || Pl3xMap.api().getIconRegistry().has(shadow), String.format("Icon not in registry (%s)", shadow));
        this.shadow = shadow;
        return this;
    }

    /**
     * Get the retina sized shadow image to use for this icon.
     * <p>
     * This shadow image will be used on retina devices.
     * <p>
     * Key must be registered with the icon registry.
     * <p>
     * Defaults to '<code>{@link #getShadow()}</code>' if null.
     *
     * @return retina shadow image
     * @see IconRegistry
     */
    @Nullable
    public Key getShadowRetina() {
        return this.shadowRetina;
    }

    /**
     * Set the retina sized shadow image to use for this icon.
     * <p>
     * This shadow image will be used on retina devices.
     * <p>
     * Key must be registered with the icon registry.
     * <p>
     * Defaults to '<code>{@link #getShadow()}</code>' if null.
     *
     * @param shadowRetina new retina shadow image
     * @return this icon
     * @see IconRegistry
     */
    @NotNull
    public Icon setShadowRetina(@Nullable Key shadowRetina) {
        Preconditions.checkArgument(shadowRetina == null || Pl3xMap.api().getIconRegistry().has(shadowRetina), String.format("Icon not in registry (%s)", shadowRetina));
        this.shadowRetina = shadowRetina;
        return this;
    }

    /**
     * Get the size of the shadow image in pixels.
     * <p>
     * Used for auto centering the shadow image on '<code>{@link #getPoint()}</code>' if set.
     *
     * @return shadow image size
     */
    @Nullable
    public Vector getShadowSize() {
        return this.shadowSize;
    }

    /**
     * Set the size of the shadow image in pixels.
     * <p>
     * Used for auto centering the shadow image on '<code>{@link #getPoint()}</code>' if set.
     *
     * @param shadowSize new shadow image size
     * @return this icon
     */
    @NotNull
    public Icon setShadowSize(@Nullable Vector shadowSize) {
        this.shadowSize = shadowSize;
        return this;
    }

    /**
     * Get the coordinates of the "tip" of the shadow image (relative to its top left corner).
     * <p>
     * The icon will be aligned so that this point is at {@link #getPoint()}.
     * <p>
     * The same as {@link #getAnchor()} if null.
     *
     * @return icon anchor
     */
    @Nullable
    public Vector getShadowAnchor() {
        return this.shadowAnchor;
    }

    /**
     * Set the coordinates of the "tip" of the shadow image (relative to its top left corner).
     * <p>
     * The shadow image will be aligned so that this point is at {@link #getPoint()}.
     * <p>
     * The same as {@link #getAnchor()} if null.
     *
     * @param shadowAnchor new anchor
     * @return this icon
     */
    @NotNull
    public Icon setShadowAnchor(@Nullable Vector shadowAnchor) {
        this.shadowAnchor = shadowAnchor;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArrayWrapper wrapper = new JsonArrayWrapper();
        wrapper.add(getPoint());
        wrapper.add(getImage());
        wrapper.add(getRetina());
        wrapper.add(getSize());
        wrapper.add(getAnchor());
        wrapper.add(getShadow());
        wrapper.add(getShadowRetina());
        wrapper.add(getShadowSize());
        wrapper.add(getShadowAnchor());
        return wrapper.getJsonArray();
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
        return getPoint().equals(other.getPoint())
                && getImage().equals(other.getImage())
                && Objects.equals(getRetina(), other.getRetina())
                && Objects.equals(getSize(), other.getSize())
                && Objects.equals(getAnchor(), other.getAnchor())
                && Objects.equals(getShadow(), other.getShadow())
                && Objects.equals(getShadowRetina(), other.getShadowRetina())
                && Objects.equals(getShadowSize(), other.getShadowSize())
                && Objects.equals(getShadowAnchor(), other.getShadowAnchor())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPoint(), getImage(), getRetina(), getSize(), getAnchor(), getShadow(),
                getShadowRetina(), getShadowAnchor(), getShadowSize(), getOptions());
    }

    @Override
    public String toString() {
        return "Icon{"
                + "point=" + getPoint()
                + ",image=" + getImage()
                + ",retina=" + getRetina()
                + ",size=" + getSize()
                + ",anchor=" + getAnchor()
                + ",shadow=" + getShadow()
                + ",shadowRetina=" + getShadowRetina()
                + ",shadowAnchor=" + getShadowAnchor()
                + ",shadowSize=" + getShadowSize()
                + ",options=" + getOptions()
                + "}";
    }
}
