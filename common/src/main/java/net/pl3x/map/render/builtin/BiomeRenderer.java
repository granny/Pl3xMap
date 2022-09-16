package net.pl3x.map.render.builtin;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.pl3x.map.configuration.AdvancedConfig;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.image.Image;
import net.pl3x.map.render.Renderer;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.ScanData;
import net.pl3x.map.render.ScanTask;
import net.pl3x.map.util.Colors;

public final class BiomeRenderer extends Renderer {
    public BiomeRenderer(RendererHolder holder, ScanTask scanTask) {
        super(holder, scanTask);
    }

    @Override
    public void scanData(RegionCoordinate region, ScanData.Data scanData) {
        for (ScanData data : scanData.values()) {
            int pixelColor = 0;

            if (Colors.getRawBlockColor(data.getBlockState()) > 0) {
                boolean fluid = data.getFluidPos() != null;

                // determine the biome
                ResourceKey<Biome> biomeKey = fluid ? data.getFluidBiomeKey() : data.getBlockBiomeKey();
                pixelColor = biomeKey == null ? 0 : Colors.setAlpha(0xFF, AdvancedConfig.BIOME_COLORS.getOrDefault(biomeKey, 0));

                // work out the heightmap
                if (!fluid) {
                    pixelColor = Colors.mix(pixelColor, getHeightmap().getColor(data.getCoordinate(), data, scanData));
                }
            }

            int pixelX = data.getCoordinate().getBlockX() & Image.SIZE - 1;
            int pixelZ = data.getCoordinate().getBlockZ() & Image.SIZE - 1;

            getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
        }
    }
}
