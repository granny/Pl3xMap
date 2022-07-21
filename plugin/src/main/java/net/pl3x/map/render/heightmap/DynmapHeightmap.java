package net.pl3x.map.render.heightmap;

import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.util.Colors;

public class DynmapHeightmap extends Heightmap {
    public int getColor(ScanData data1, ScanData data2, ScanData data3, boolean flat) {
        int heightColor = 0x22;
        if (!flat && data1.getBlockPos().getY() % 2 == 1) {
            heightColor = 0x33;
        }
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
