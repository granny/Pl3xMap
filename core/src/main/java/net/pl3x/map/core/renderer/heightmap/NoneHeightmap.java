package net.pl3x.map.core.renderer.heightmap;

import net.pl3x.map.core.world.Region;

public class NoneHeightmap extends Heightmap {
    public NoneHeightmap() {
        super("none");
    }

    @Override
    public int getColor(Region region, int blockX, int blockZ) {
        return 0x22000000;
    }
}
