package net.pl3x.map.core.markers.marker;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.JsonObjectWrapper;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.Vector;
import net.pl3x.map.core.registry.IconRegistry;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents an icon marker.
 */
public class Icon extends Marker<Icon> {
    private Point point;
    private String image;
    private String retina;
    private Vector size;
    private Vector anchor;
    private Double rotationAngle;
    private String rotationOrigin;
    private String shadow;
    private String shadowRetina;
    private Vector shadowSize;
    private Vector shadowAnchor;

    private Icon(@NonNull String key) {
        super("icon", key);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     */
    public Icon(@NonNull String key, double x, double z, @NonNull String image) {
        this(key);
        setPoint(Point.of(x, z));
        setImage(image);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     */
    public Icon(@NonNull String key, @NonNull Point point, @NonNull String image) {
        this(key);
        setPoint(point);
        setImage(image);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @param size  size of image
     */
    public Icon(@NonNull String key, double x, double z, @NonNull String image, double size) {
        this(key);
        setPoint(Point.of(x, z));
        setImage(image);
        setSize(Vector.of(size, size));
    }

    /**
     * Create a new icon.
     *
     * @param key    identifying key
     * @param x      icon x location on map
     * @param z      icon z location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     */
    public Icon(@NonNull String key, double x, double z, @NonNull String image, double width, double height) {
        this(key);
        setPoint(Point.of(x, z));
        setImage(image);
        setSize(Vector.of(width, height));
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     */
    public Icon(@NonNull String key, @NonNull Point point, @NonNull String image, double size) {
        this(key);
        setPoint(point);
        setImage(image);
        setSize(Vector.of(size, size));
    }

    /**
     * Create a new icon.
     *
     * @param key    identifying key
     * @param point  icon location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     */
    public Icon(@NonNull String key, @NonNull Point point, @NonNull String image, double width, double height) {
        this(key);
        setPoint(point);
        setImage(image);
        setSize(Vector.of(width, height));
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     */
    public Icon(@NonNull String key, @NonNull Point point, @NonNull String image, @Nullable Vector size) {
        this(key);
        setPoint(point);
        setImage(image);
        setSize(size);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @return a new icon
     */
    @NonNull
    public static Icon of(@NonNull String key, double x, double z, @NonNull String image) {
        return new Icon(key, x, z, image);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @return a new icon
     */
    @NonNull
    public static Icon of(@NonNull String key, @NonNull Point point, @NonNull String image) {
        return new Icon(key, point, image);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param x     icon x location on map
     * @param z     icon z location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    @NonNull
    public static Icon of(@NonNull String key, double x, double z, @NonNull String image, double size) {
        return new Icon(key, x, z, image, size);
    }

    /**
     * Create a new icon.
     *
     * @param key    identifying key
     * @param x      icon x location on map
     * @param z      icon z location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    @NonNull
    public static Icon of(@NonNull String key, double x, double z, @NonNull String image, double width, double height) {
        return new Icon(key, x, z, image, width, height);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    @NonNull
    public static Icon of(@NonNull String key, @NonNull Point point, @NonNull String image, double size) {
        return new Icon(key, point, image, size);
    }

    /**
     * Create a new icon.
     *
     * @param key    identifying key
     * @param point  icon location on map
     * @param image  image key
     * @param width  width of image
     * @param height height of image
     * @return a new icon
     */
    @NonNull
    public static Icon of(@NonNull String key, @NonNull Point point, @NonNull String image, double width, double height) {
        return new Icon(key, point, image, width, height);
    }

    /**
     * Create a new icon.
     *
     * @param key   identifying key
     * @param point icon location on map
     * @param image image key
     * @param size  size of image
     * @return a new icon
     */
    @NonNull
    public static Icon of(@NonNull String key, @NonNull Point point, @NonNull String image, @Nullable Vector size) {
        return new Icon(key, point, image, size);
    }

    /**
     * Get the {@link Point} on the map for this icon.
     *
     * @return point on map
     */
    @NonNull
    public Point getPoint() {
        return this.point;
    }

    /**
     * Set a new {@link Point} on the map for this icon.
     *
     * @param point new point on map
     * @return this icon
     */
    @NonNull
    public Icon setPoint(@NonNull Point point) {
        this.point = Preconditions.checkNotNull(point, "Icon point is null");
        return this;
    }

    /**
     * Get the image to use for this icon.
     *
     * @return image
     * @see IconRegistry
     */
    @NonNull
    public String getImage() {
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
    @NonNull
    public Icon setImage(@NonNull String image) {
        Preconditions.checkNotNull(image, "Icon key is null");
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
    public String getRetina() {
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
    @NonNull
    public Icon setRetina(@Nullable String retina) {
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
    @NonNull
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
    @NonNull
    public Icon setAnchor(@Nullable Vector anchor) {
        this.anchor = anchor;
        return this;
    }

    /**
     * Get the rotation angle, in degrees, clockwise.
     * <p>
     * Defaults to '<code>0</code>' if null.
     *
     * @return angle of rotation
     */
    @Nullable
    public Double getRotationAngle() {
        return this.rotationAngle;
    }

    /**
     * Set the rotation angle, in degrees, clockwise.
     * <p>
     * Defaults to '<code>0</code>' if null.
     *
     * @param rotationAngle angle of rotation
     * @return this icon
     */
    @NonNull
    public Icon setRotationAngle(@Nullable Double rotationAngle) {
        this.rotationAngle = rotationAngle;
        return this;
    }

    /**
     * Get the rotation origin, as a transform-origin CSS rule.
     * <p>
     * Defaults to '<code>bottom center</code>' if null.
     *
     * @return origin of rotation
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform-origin">MDN transform-origin</a>
     */
    @Nullable
    public String getRotationOrigin() {
        return this.rotationOrigin;
    }

    /**
     * Set the rotation origin, as a transform-origin CSS rule.
     * <p>
     * Defaults to '<code>bottom center</code>' if null.
     *
     * @param rotationOrigin origin of rotation
     * @return this icon
     * @see <a href="https://developer.mozilla.org/en-US/docs/Web/CSS/transform-origin">MDN transform-origin</a>
     */
    @NonNull
    public Icon setRotationOrigin(@Nullable String rotationOrigin) {
        this.rotationOrigin = rotationOrigin;
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
    public String getShadow() {
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
    @NonNull
    public Icon setShadow(@Nullable String shadow) {
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
    public String getShadowRetina() {
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
    @NonNull
    public Icon setShadowRetina(@Nullable String shadowRetina) {
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
    @NonNull
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
    @NonNull
    public Icon setShadowAnchor(@Nullable Vector shadowAnchor) {
        this.shadowAnchor = shadowAnchor;
        return this;
    }

    @Override
    @NonNull
    public JsonElement toJson() {
        JsonObjectWrapper wrapper = new JsonObjectWrapper();
        wrapper.addProperty("key", getKey());
        wrapper.addProperty("point", getPoint());
        wrapper.addProperty("image", getImage());
        wrapper.addProperty("retina", getRetina());
        wrapper.addProperty("size", getSize());
        wrapper.addProperty("anchor", getAnchor());
        wrapper.addProperty("shadow", getShadow());
        wrapper.addProperty("shadowRetina", getShadowRetina());
        wrapper.addProperty("shadowSize", getShadowSize());
        wrapper.addProperty("shadowAnchor", getShadowAnchor());
        wrapper.addProperty("rotationAngle", getRotationAngle());
        wrapper.addProperty("rotationOrigin", getRotationOrigin());
        wrapper.addProperty("pane", getPane());
        return wrapper.getJsonObject();
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
        return getKey().equals(other.getKey())
                && getPoint().equals(other.getPoint())
                && getImage().equals(other.getImage())
                && Objects.equals(getRetina(), other.getRetina())
                && Objects.equals(getSize(), other.getSize())
                && Objects.equals(getAnchor(), other.getAnchor())
                && Objects.equals(getRotationAngle(), other.getRotationAngle())
                && Objects.equals(getRotationOrigin(), other.getRotationOrigin())
                && Objects.equals(getShadow(), other.getShadow())
                && Objects.equals(getShadowRetina(), other.getShadowRetina())
                && Objects.equals(getShadowSize(), other.getShadowSize())
                && Objects.equals(getShadowAnchor(), other.getShadowAnchor())
                && Objects.equals(getPane(), other.getPane())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getPoint(), getImage(), getRetina(), getSize(), getAnchor(), getRotationAngle(),
                getRotationOrigin(), getShadow(), getShadowRetina(), getShadowSize(), getShadowAnchor(), getPane(), getOptions());
    }

    @Override
    @NonNull
    public String toString() {
        return "Icon{"
                + "key=" + getKey()
                + ",point=" + getPoint()
                + ",image=" + getImage()
                + ",retina=" + getRetina()
                + ",size=" + getSize()
                + ",anchor=" + getAnchor()
                + ",rotationAngle=" + getRotationAngle()
                + ",rotationOrigin=" + getRotationOrigin()
                + ",shadow=" + getShadow()
                + ",shadowRetina=" + getShadowRetina()
                + ",shadowSize=" + getShadowSize()
                + ",shadowAnchor=" + getShadowAnchor()
                + ",pane=" + getPane()
                + ",options=" + getOptions()
                + "}";
    }
}
