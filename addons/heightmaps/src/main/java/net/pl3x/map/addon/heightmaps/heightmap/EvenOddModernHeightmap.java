package net.pl3x.map.addon.heightmaps.heightmap;

import net.pl3x.map.api.coordinate.BlockCoordinate;
import net.pl3x.map.api.heightmap.Heightmap;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.util.Colors;

public class EvenOddModernHeightmap extends Heightmap {
    @Override
    public int getColor(BlockCoordinate coordinate, ScanData data, ScanData.Data scanData) {
        int heightColor = 0x22;
        heightColor = getColor(data, scanData.get(coordinate.west()), heightColor, 0x22);
        heightColor = getColor(data, scanData.get(coordinate.north()), heightColor, 0x22);
        if (data.getBlockPos().getY() % 2 == 1) {
            heightColor += 0x11;
        }
        return Colors.setAlpha(heightColor, 0x000000);
    }
}
