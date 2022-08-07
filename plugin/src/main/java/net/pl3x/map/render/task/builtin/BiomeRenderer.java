package net.pl3x.map.render.task.builtin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.pl3x.map.api.coordinate.RegionCoordinate;
import net.pl3x.map.api.image.Image;
import net.pl3x.map.configuration.Advanced;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.render.task.ScanData;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.util.Colors;

public final class BiomeRenderer extends Renderer {
    public BiomeRenderer(String name, ScanTask scanTask) {
        super(name, scanTask);
    }

    @Override
    public void scanData(RegionCoordinate region, ScanData.Data scanData) {
        for (ScanData data : scanData.values()) {
            boolean fluid = data.getFluidPos() != null;

            // determine the biome
            ResourceKey<Biome> biomeKey = fluid ? data.getFluidBiomeKey() : data.getBlockBiomeKey();
            int pixelColor = biomeKey == null ? 0 : Colors.setAlpha(0xFF, Advanced.BIOME_COLORS.getOrDefault(biomeKey, 0));

            // work out the heightmap
            pixelColor = Colors.mix(pixelColor, getHeightmap().getColor(data.getCoordinate(), data, scanData, fluid));

            int pixelX = data.getCoordinate().getBlockX() & Image.SIZE - 1;
            int pixelZ = data.getCoordinate().getBlockZ() & Image.SIZE - 1;

            getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
        }
    }
}
