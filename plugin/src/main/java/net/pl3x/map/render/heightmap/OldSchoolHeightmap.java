package net.pl3x.map.render.heightmap;

import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.util.Colors;

public class OldSchoolHeightmap extends Heightmap {
    public int getColor(ScanData data1, ScanData data2, ScanData data3, boolean flat) {
        int heightColor = 0x22;
        if (!flat) {
            if (data2 != null) {
                if (data1.getBlockPos().getY() > data2.getBlockPos().getY()) {
                    heightColor = 0x00;
                } else if (data1.getBlockPos().getY() < data2.getBlockPos().getY()) {
                    heightColor = 0x44;
                }
            }
        }
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
