package net.pl3x.map.heightmap;

import net.pl3x.map.registry.KeyedRegistry;

public class HeightmapRegistry extends KeyedRegistry<Heightmap> {
    public void register() {
        register(new EvenOddHeightmap());
        register(new ModernHeightmap());
        register(new OldSchoolHeightmap());
    }
}
