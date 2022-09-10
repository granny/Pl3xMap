package net.pl3x.map.heightmap;

import net.pl3x.map.Key;
import net.pl3x.map.coordinate.BlockCoordinate;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.util.Colors;

public class EvenOddHeightmap extends Heightmap {
    public EvenOddHeightmap() {
        super(new Key("even_odd-heightmap"));
    }

    @Override
    public int getColor(BlockCoordinate coordinate, ScanData data, ScanData.Data scanData) {
        int heightColor = 0x22;
        if (data.getBlockPos().getY() % 2 == 1) {
            heightColor = 0x33;
        }
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
