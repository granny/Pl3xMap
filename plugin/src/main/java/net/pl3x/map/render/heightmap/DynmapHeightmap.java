package net.pl3x.map.render.heightmap;

import net.minecraft.core.BlockPos;
import net.pl3x.map.util.Colors;

public class DynmapHeightmap extends Heightmap {
    public int getColor(BlockPos pos, int x, int z, boolean flat) {
        int heightColor = 0x22;
        if (!flat && pos.getY() % 2 == 1) {
            heightColor = 0x33;
        }
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
