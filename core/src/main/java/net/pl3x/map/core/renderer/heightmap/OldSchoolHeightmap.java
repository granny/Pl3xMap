package net.pl3x.map.core.renderer.heightmap;

import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;

public class OldSchoolHeightmap extends Heightmap {
    public OldSchoolHeightmap() {
        super("old_school");
    }

    @Override
    public int getColor(Region region, int blockX, int blockZ) {
        Chunk.BlockData data1 = region.getWorld().getChunk(region, blockX >> 4, blockZ >> 4).getData(blockX, blockZ);
        Chunk.BlockData data2 = region.getWorld().getChunk(region, (blockX - 1) >> 4, blockZ >> 4).getData(blockX - 1, blockZ);
        int heightColor = 0x22;
        if (data1 != null && data2 != null) {
            heightColor = getColor(data1.getBlockY(), data2.getBlockY(), heightColor, 0x22);
        }
        return heightColor << 24;
    }
}
