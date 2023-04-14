package net.pl3x.map.core.heightmap;

import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;

public class ModernHeightmap extends Heightmap {
    public ModernHeightmap() {
        super("modern");
    }

    @Override
    public int getColor(Region region, int blockX, int blockZ) {
        Chunk.BlockData data1 = region.getWorld().getChunk(region, blockX >> 4, blockZ >> 4).getData(blockX, blockZ);
        Chunk.BlockData data2 = region.getWorld().getChunk(region, (blockX - 1) >> 4, blockZ >> 4).getData(blockX - 1, blockZ);
        Chunk.BlockData data3 = region.getWorld().getChunk(region, blockX >> 4, (blockZ - 1) >> 4).getData(blockX, blockZ - 1);
        int heightColor = 0x22;
        if (data1 != null) {
            int y = data1.getBlockY();
            if (data2 != null) {
                heightColor = getColor(y, data2.getBlockY(), heightColor, 0x22);
            }
            if (data3 != null) {
                heightColor = getColor(y, data3.getBlockY(), heightColor, 0x22);
            }
        }
        return heightColor << 24;
    }
}
