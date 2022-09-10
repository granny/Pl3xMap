package net.pl3x.map.addon.heightmaps.heightmap;

import net.pl3x.map.coordinate.BlockCoordinate;
import net.pl3x.map.heightmap.Heightmap;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.util.Colors;

public class EvenOddOldSchoolHeightmap extends Heightmap {
    public EvenOddOldSchoolHeightmap() {
        super("even_odd_old_school");
    }

    @Override
    public int getColor(BlockCoordinate coordinate, ScanData data, ScanData.Data scanData) {
        int heightColor = 0x22;
        heightColor = getColor(data, scanData.get(coordinate.west()), heightColor, 0x22);
        if (data.getBlockPos().getY() % 2 == 1) {
            heightColor += 0x11;
        }
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
