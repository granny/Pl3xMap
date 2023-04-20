package net.pl3x.map.core.renderer.heightmap;

import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.NonNull;

public class NoneHeightmap extends Heightmap {
    public NoneHeightmap() {
        super("none");
    }

    @Override
    public int getColor(@NonNull Region region, int blockX, int blockZ) {
        return 0x22000000;
    }
}
