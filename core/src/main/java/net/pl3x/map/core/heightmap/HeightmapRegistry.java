package net.pl3x.map.core.heightmap;

import net.pl3x.map.core.registry.Registry;

public class HeightmapRegistry extends Registry<Heightmap> {
    public void register() {
        register(new EvenOddHeightmap());
        register(new ModernHeightmap());
        register(new NoneHeightmap());
        register(new OldSchoolHeightmap());
    }

    public void register(Heightmap heightmap) {
        register(heightmap.getKey(), heightmap);
    }
}
