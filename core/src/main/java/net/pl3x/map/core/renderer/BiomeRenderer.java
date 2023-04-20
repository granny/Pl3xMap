package net.pl3x.map.core.renderer;

import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.renderer.task.RegionScanTask;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class BiomeRenderer extends Renderer {
    public BiomeRenderer(@NonNull RegionScanTask task, @NonNull Builder builder) {
        super(task, builder);
    }

    @Override
    public void scanBlock(@NonNull Region region, @NonNull Chunk chunk, Chunk.@NonNull BlockData data, int blockX, int blockZ) {
        int pixelColor = 0;

        if (Colors.getRawBlockColor(data.getBlockState().getBlock()) > 0) {
            // determine the biome
            Biome biome = data.getBiome(region, blockX, blockZ);
            int color = ColorsConfig.BIOME_COLORS.getOrDefault(biome.id(), 0);
            pixelColor = Colors.setAlpha(0xFF, color);

            // work out the heightmap
            if (data.getFluidState() == null) {
                pixelColor = Colors.blend(getHeightmap().getColor(region, blockX, blockZ), pixelColor);
            }
        }

        getTileImage().setPixel(blockX, blockZ, pixelColor);
    }
}
