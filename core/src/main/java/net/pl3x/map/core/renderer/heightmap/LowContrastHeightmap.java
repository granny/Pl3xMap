package net.pl3x.map.core.renderer.heightmap;

import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.NonNull;

public class LowContrastHeightmap extends Heightmap {
    public LowContrastHeightmap() {
        super("low_contrast");
    }

    @Override
    @SuppressWarnings("DuplicatedCode")
    public int getColor(@NonNull Region region, int blockX, int blockZ) {
        Chunk.BlockData origin = region.getWorld().getChunk(region, blockX >> 4, blockZ >> 4).getData(blockX, blockZ);
        Chunk.BlockData west = region.getWorld().getChunk(region, (blockX - 1) >> 4, blockZ >> 4).getData(blockX - 1, blockZ);
        Chunk.BlockData north = region.getWorld().getChunk(region, blockX >> 4, (blockZ - 1) >> 4).getData(blockX, blockZ - 1);
        int heightColor = 0x22;
        if (origin != null) {
            int y = origin.getBlockY();
            if (west != null) {
                heightColor = getColor(y, west.getBlockY(), heightColor, 0x11);
            }
            if (north != null) {
                heightColor = getColor(y, north.getBlockY(), heightColor, 0x11);
            }
        }
        return heightColor << 24;
    }
}
