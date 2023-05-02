package net.pl3x.map.core.markers.area;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Rectangle implements Area {
    private final int minX;
    private final int minZ;
    private final int maxX;
    private final int maxZ;

    public Rectangle(int minx, int z1, int x2, int z2) {
        this.minX = Math.min(minx, x2);
        this.minZ = Math.min(z1, z2);
        this.maxX = Math.max(minx, x2);
        this.maxZ = Math.max(z1, z2);
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMaxZ() {
        return this.maxZ;
    }

    @Override
    public boolean containsBlock(int blockX, int blockZ) {
        return blockX >= getMinX() && blockX <= getMaxX() && blockZ >= getMinZ() && blockZ <= getMaxZ();
    }

    @Override
    public boolean containsChunk(int chunkX, int chunkZ) {
        return chunkX >= (getMinX() >> 4) && chunkX <= (getMaxX() >> 4) && chunkZ >= (getMinZ() >> 4) && chunkZ <= (getMaxZ() >> 4);
    }

    @Override
    public boolean containsRegion(int regionX, int regionZ) {
        return regionX >= (getMinX() >> 9) && regionX <= (getMaxX() >> 9) && regionZ >= (getMinZ() >> 9) && regionZ <= (getMaxZ() >> 9);
    }

    @Override
    public @NonNull Map<String, Object> serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("type", "rectangle");
        map.put("min-x", getMinX());
        map.put("min-z", getMinZ());
        map.put("max-x", getMaxX());
        map.put("max-z", getMaxZ());
        return map;
    }

    public static @NonNull Rectangle deserialize(Map<String, Object> map) {
        return new Rectangle(
                (int) map.get("min-x"),
                (int) map.get("min-z"),
                (int) map.get("max-x"),
                (int) map.get("max-z")
        );
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
        Rectangle other = (Rectangle) o;
        return getMinX() == other.getMinX() &&
                getMinZ() == other.getMinZ() &&
                getMaxX() == other.getMaxX() &&
                getMaxZ() == other.getMaxZ();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMinX(), getMinZ(), getMaxX(), getMaxZ());
    }

    @Override
    public @NonNull String toString() {
        return "Rectangle{"
                + "minX=" + getMinX()
                + ",minZ=" + getMinZ()
                + ",maxX=" + getMaxX()
                + ",maxZ=" + getMaxZ()
                + "}";
    }
}
