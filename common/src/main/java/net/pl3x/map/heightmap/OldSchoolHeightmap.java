package net.pl3x.map.heightmap;

import net.pl3x.map.coordinate.BlockCoordinate;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.util.Colors;

public class OldSchoolHeightmap extends Heightmap {
    public OldSchoolHeightmap() {
        super("old_school");
    }

    @Override
    public int getColor(BlockCoordinate coordinate, ScanData data, ScanData.Data scanData) {
        int heightColor = 0x22;
        heightColor = getColor(data, scanData.get(coordinate.west()), heightColor, 0x22);
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
