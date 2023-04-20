package net.pl3x.map.core.renderer.heightmap;

import net.pl3x.map.core.registry.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HeightmapRegistry extends Registry<Heightmap> {
    public void register() {
        register(new EvenOddHeightmap());
        register(new EvenOddLowContrastHeightmap());
        register(new EvenOddModernHeightmap());
        register(new EvenOddOldSchoolHeightmap());
        register(new LowContrastHeightmap());
        register(new ModernHeightmap());
        register(new NoneHeightmap());
        register(new OldSchoolHeightmap());
    }

    public void register(@NonNull Heightmap heightmap) {
        register(heightmap.getKey(), heightmap);
    }
}
