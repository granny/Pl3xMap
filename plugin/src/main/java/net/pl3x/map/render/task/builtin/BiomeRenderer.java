package net.pl3x.map.render.task.builtin;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.configuration.Advanced;
import net.pl3x.map.render.heightmap.Heightmap;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.MapWorld;

public final class BiomeRenderer extends Renderer {
    public BiomeRenderer(String name, Render render, RegionCoordinate region) {
        super(name, render, region);
    }

    @Override
    public void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState blockState, BlockPos blockPos, Biome blockBiome, BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, int x, int z, List<Integer> glass, Heightmap heightmap, int color) {
        // fluid stuff
        boolean isFluid = fluidPos != null;
        boolean transFluid = getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS;
        boolean flatFluid = isFluid && !transFluid;

        // determine the biome
        ResourceKey<Biome> biomeKey = BiomeColors.getBiomeRegistry(mapWorld.getLevel()).getResourceKey(flatFluid ? fluidBiome : blockBiome).orElse(null);
        int pixelColor = biomeKey == null ? 0 : Colors.setAlpha(0xFF, Advanced.BIOME_COLORS.getOrDefault(biomeKey, 0));

        // work out the heightmap
        pixelColor = Colors.mix(pixelColor, heightmap.getColor(blockPos, x, z, flatFluid));

        // fancy fluids, yum
        if (isFluid && transFluid) {
            int fluidColor = fancyFluids(fluidState, fluidPos, fluidBiome, (fluidPos.getY() - blockPos.getY()) * 0.025F);
            pixelColor = Colors.mix(pixelColor, fluidColor);
        }

        int pixelX = blockPos.getX() & Image.SIZE - 1;
        int pixelZ = blockPos.getZ() & Image.SIZE - 1;

        getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
    }
}
