package net.pl3x.map.render.heightmap;

import net.minecraft.core.BlockPos;
import net.pl3x.map.util.Colors;

public class ModernHeightmap extends Heightmap {
    public int getColor(BlockPos pos, int x, int z, boolean flat) {
        int heightColor = 0x22;
        if (!flat) {
            if (this.x[x] != Integer.MAX_VALUE) {
                if (pos.getY() > this.x[x]) {
                    heightColor = 0x00;
                } else if (pos.getY() < this.x[x]) {
                    heightColor = 0x44;
                }
            }
            if (this.z[z] != Integer.MAX_VALUE) {
                if (pos.getY() > this.z[z]) {
                    heightColor = 0x00;
                } else if (pos.getY() < this.z[z]) {
                    heightColor = 0x44;
                }
            }
        }
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
