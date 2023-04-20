package net.pl3x.map.core.renderer.heightmap;

import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.NonNull;

public class EvenOddHeightmap extends Heightmap {
    public EvenOddHeightmap() {
        super("even_odd");
    }

    @Override
    public int getColor(@NonNull Region region, int blockX, int blockZ) {
        Chunk.BlockData origin = region.getWorld().getChunk(region, blockX >> 4, blockZ >> 4).getData(blockX, blockZ);
        int heightColor = 0x22;
        if (origin != null && origin.getBlockY() % 2 == 1) {
            heightColor = 0x33;
        }
        return heightColor << 24;
    }
}
