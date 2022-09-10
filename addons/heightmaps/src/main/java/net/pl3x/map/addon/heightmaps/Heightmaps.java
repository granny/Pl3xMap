package net.pl3x.map.addon.heightmaps;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.addon.heightmaps.heightmap.EvenOddLowContrastHeightmap;
import net.pl3x.map.addon.heightmaps.heightmap.EvenOddModernHeightmap;
import net.pl3x.map.addon.heightmaps.heightmap.EvenOddOldSchoolHeightmap;
import net.pl3x.map.addon.heightmaps.heightmap.LowContrastHeightmap;
import net.pl3x.map.heightmap.HeightmapRegistry;

public class Heightmaps extends Addon {
    @Override
    public void onEnable() {
        // register our custom heightmaps with Pl3xMap
        HeightmapRegistry registry = Pl3xMap.api().getHeightmapRegistry();
        registry.register(new EvenOddLowContrastHeightmap());
        registry.register(new EvenOddModernHeightmap());
        registry.register(new EvenOddOldSchoolHeightmap());
        registry.register(new LowContrastHeightmap());
    }

    @Override
    public void onDisable() {
        // unregister our custom heightmaps from Pl3xMap
        HeightmapRegistry registry = Pl3xMap.api().getHeightmapRegistry();
        registry.unregister(EvenOddLowContrastHeightmap.KEY);
        registry.unregister(EvenOddModernHeightmap.KEY);
        registry.unregister(EvenOddOldSchoolHeightmap.KEY);
        registry.unregister(LowContrastHeightmap.KEY);
    }
}
