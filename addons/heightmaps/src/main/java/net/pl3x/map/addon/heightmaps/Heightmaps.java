package net.pl3x.map.addon.heightmaps;

import java.util.List;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.addon.heightmaps.heightmap.EvenOddLowContrastHeightmap;
import net.pl3x.map.addon.heightmaps.heightmap.EvenOddModernHeightmap;
import net.pl3x.map.addon.heightmaps.heightmap.EvenOddOldSchoolHeightmap;
import net.pl3x.map.addon.heightmaps.heightmap.LowContrastHeightmap;
import net.pl3x.map.heightmap.Heightmap;
import net.pl3x.map.heightmap.HeightmapRegistry;

public class Heightmaps extends Addon {
    private final List<Heightmap> heightmaps = List.of(
            new EvenOddLowContrastHeightmap(),
            new EvenOddModernHeightmap(),
            new EvenOddOldSchoolHeightmap(),
            new LowContrastHeightmap()
    );

    @Override
    public void onEnable() {
        // register our custom heightmaps with Pl3xMap
        HeightmapRegistry registry = Pl3xMap.api().getHeightmapRegistry();
        this.heightmaps.forEach(registry::register);
    }

    @Override
    public void onDisable() {
        // unregister our custom heightmaps from Pl3xMap
        HeightmapRegistry registry = Pl3xMap.api().getHeightmapRegistry();
        this.heightmaps.forEach(registry::register);
    }
}
