package net.pl3x.map.core.renderer;

import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import net.pl3x.map.core.world.World;

public final class BiomeRenderer extends Renderer {
    public BiomeRenderer(World world, Builder builder) {
        super(world, builder);
    }

    @Override
    public void scanBlock(Region region, Chunk chunk, Chunk.BlockData data, int blockX, int blockZ) {
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
