package net.pl3x.map.render;

import net.minecraft.world.level.border.WorldBorder;
import net.pl3x.map.api.coordinate.Coordinate;
import net.pl3x.map.util.Mathf;

public class Area {
    private final int minX;
    private final int maxX;
    private final int minZ;
    private final int maxZ;

    public Area(int minX, int minZ, int maxX, int maxZ) {
        this.minX = Coordinate.blockToChunk(Math.min(minX, maxX));
        this.minZ = Coordinate.blockToChunk(Math.min(minZ, maxZ));
        this.maxX = Coordinate.blockToChunk(Math.max(minX, maxX));
        this.maxZ = Coordinate.blockToChunk(Math.max(minZ, maxZ));
    }

    public Area(WorldBorder worldBorder) {
        this.minX = Coordinate.blockToChunk((int) Math.floor(worldBorder.getMinX()));
        this.minZ = Coordinate.blockToChunk((int) Math.floor(worldBorder.getMinZ()));
        this.maxX = Coordinate.blockToChunk((int) Math.ceil(worldBorder.getMaxX()));
        this.maxZ = Coordinate.blockToChunk((int) Math.ceil(worldBorder.getMaxZ()));
    }

    public int getMinX() {
        return this.minX;
    }

    public int getMaxX() {
        return this.maxX;
    }

    public int getMinZ() {
        return this.minZ;
    }

    public int getMaxZ() {
        return this.maxZ;
    }

    public boolean containsChunk(int chunkX, int chunkZ) {
        return !(chunkX < getMinX() || chunkX > getMaxX() || chunkZ < getMinZ() || chunkZ > getMaxZ());
    }

    public boolean containsRegion(int regionX, int regionZ) {
        return !(regionX < Coordinate.chunkToRegion(getMinX()) ||
                regionX > Coordinate.chunkToRegion(getMaxX()) ||
                regionZ < Coordinate.chunkToRegion(getMinZ()) ||
                regionZ > Coordinate.chunkToRegion(getMaxZ()));
    }
}
