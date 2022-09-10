package net.pl3x.map.heightmap;

import net.pl3x.map.Registry;

public class HeightmapRegistry extends Registry<Heightmap> {
    public void register() {
        register(new EvenOddHeightmap());
        register(new ModernHeightmap());
        register(new OldSchoolHeightmap());
    }
}
