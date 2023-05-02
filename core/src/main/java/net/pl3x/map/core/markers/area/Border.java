package net.pl3x.map.core.markers.area;

import java.util.LinkedHashMap;
import java.util.Map;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Border implements Area {
    private final World world;

    public Border(World world) {
        this.world = world;
    }

    public int getMinX() {
        return (int) this.world.getBorderMinX();
    }

    public int getMinZ() {
        return (int) this.world.getBorderMinZ();
    }

    public int getMaxX() {
        return (int) this.world.getBorderMaxX();
    }

    public int getMaxZ() {
        return (int) this.world.getBorderMaxZ();
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
        map.put("type", "world-border");
        return map;
    }

    public static @NonNull Border deserialize(World world, @SuppressWarnings("unused") Map<String, Object> map) {
        return new Border(world);
    }

    @Override
    public @NonNull String toString() {
        return "Border{"
                + "minX=" + getMinX()
                + ",minZ=" + getMinZ()
                + ",maxX=" + getMaxX()
                + ",maxZ=" + getMaxZ()
                + "}";
    }
}
