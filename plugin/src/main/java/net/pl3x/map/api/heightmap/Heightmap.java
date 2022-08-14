package net.pl3x.map.api.heightmap;

import net.pl3x.map.api.coordinate.BlockCoordinate;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.util.Mathf;

public abstract class Heightmap {
    public int[] x = new int[16];
    public int[] z = new int[16];

    public abstract int getColor(BlockCoordinate coordinate, ScanData data, ScanData.Data scanData);

    public int getColor(ScanData data1, ScanData data2, int heightColor, int step) {
        if (data2 != null) {
            if (data1.getBlockPos().getY() > data2.getBlockPos().getY()) {
                heightColor -= step;
            } else if (data1.getBlockPos().getY() < data2.getBlockPos().getY()) {
                heightColor += step;
            }
        }
        return Mathf.clamp(0x00, 0x44, heightColor);
    }
}
