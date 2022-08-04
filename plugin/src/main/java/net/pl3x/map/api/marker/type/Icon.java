package net.pl3x.map.api.marker.type;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.util.Objects;
import net.pl3x.map.api.Key;
import net.pl3x.map.api.marker.Marker;
import net.pl3x.map.api.marker.Point;
import net.pl3x.map.api.registry.IconRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Icon marker.
 */
public class Icon extends Marker {
    public static Key DEFAULT_ICON = new Key("default_icon");

    private Key image;
    private Point point;
    private Point offset;
    private Key shadow;
    private Point shadowOffset;

    /**
     * Create a new default icon without shadow at 0,0.
     */
    public Icon() {
        this(Icon.DEFAULT_ICON, Point.ZERO, Point.ZERO);
    }

    /**
     * Create a new icon without shadow.
     *
     * @param image  image key
     * @param point  icon point on map
     * @param offset icon offset from point
     */
    public Icon(@NotNull Key image, @NotNull Point point, @Nullable Point offset) {
        this(image, point, offset, null, null);
    }

    /**
     * Create a new icon.
     *
     * @param image        image key
     * @param point        icon point on map
     * @param offset       icon offset from point
     * @param shadow       shadow image key
     * @param shadowOffset shadow offset from point
     */
    public Icon(@NotNull Key image, @NotNull Point point, @Nullable Point offset, @Nullable Key shadow, @Nullable Point shadowOffset) {
        super("icon");
        setImage(image);
        setPoint(point);
        setOffset(offset);
        setShadow(shadow);
        setShadowOffset(shadowOffset);
    }

    /**
     * Get the image to use for this icon.
     *
     * @return image key
     * @see IconRegistry
     */
    @NotNull
    public Key getImage() {
        return this.image;
    }

    /**
     * Set the image key to use for this icon.
     * <p>
     * Key must be registered with the icon registry.
     *
     * @param image new image key
     * @return this icon
     * @see IconRegistry
     */
    @NotNull
    public Icon setImage(@NotNull Key image) {
        Preconditions.checkNotNull(image);
        // TODO check icon registry
        this.image = image;
        return this;
    }

    /**
     * Get the point on the map for this icon.
     *
     * @return map location
     */
    @NotNull
    public Point getPoint() {
        return this.point;
    }

    /**
     * Set a new point on the map for this icon.
     *
     * @param point new point
     * @return this icon
     */
    @NotNull
    public Icon setPoint(@NotNull Point point) {
        Preconditions.checkNotNull(point);
        this.point = point;
        return this;
    }

    /**
     * Get the offset from the icon's point.
     *
     * @return icon offset
     */
    @NotNull
    public Point getOffset() {
        return this.offset;
    }

    /**
     * Set the offset from the icon's point.
     * <p>
     * Setting a null offset will reset it to 0,0.
     *
     * @param offset new offset
     * @return this icon
     */
    @NotNull
    public Icon setOffset(@Nullable Point offset) {
        this.offset = offset == null ? Point.ZERO : offset;
        return this;
    }

    /**
     * Get shadow image of this icon, if one is set.
     *
     * @return shadow image key
     */
    @Nullable
    public Key getShadow() {
        return this.shadow;
    }

    /**
     * Set the shadow image key to use for this icon.
     * <p>
     * Key must be registered with the icon registry.
     *
     * @param shadow new shadow image key
     * @return this icon
     * @see IconRegistry
     */
    @NotNull
    public Icon setShadow(@Nullable Key shadow) {
        this.shadow = shadow;
        return this;
    }

    /**
     * Get the shadow offset from the icon's point.
     *
     * @return shadow offset
     */
    @Nullable
    public Point getShadowOffset() {
        return this.shadowOffset;
    }

    /**
     * Set the shadow offset from the icon's point.
     *
     * @param shadowOffset new shadow offset
     * @return this icon
     */
    @NotNull
    public Icon setShadowOffset(@Nullable Point shadowOffset) {
        this.shadowOffset = shadowOffset;
        return this;
    }

    @Override
    @NotNull
    public JsonElement toJson() {
        JsonArray data = new JsonArray();
        data.add(getImage().getKey());
        data.add(getPoint().toJson());
        data.add(getOffset().toJson());
        if (getShadow() != null) {
            data.add(getShadow().getKey());
            data.add((getShadowOffset() == null ? Point.ZERO : getShadowOffset()).toJson());
        }
        return data;
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
                && getPoint().equals(other.getPoint())
                && getOffset().equals(other.getOffset())
                && Objects.equals(getOptions(), other.getOptions());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOptions(), getImage(), getPoint(), getOffset());
    }

    @Override
    public String toString() {
        return "Icon{image=" + getImage()
                + ",point=" + getPoint()
                + ",offset=" + getOffset()
                + ",shadow=" + getShadow()
                + ",shadowOffset=" + getShadowOffset()
                + ",options=" + getOptions()
                + "}";
    }
}
