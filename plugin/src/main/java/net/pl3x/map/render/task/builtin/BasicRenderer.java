package net.pl3x.map.render.task.builtin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.MapWorld;

public class BasicRenderer extends Renderer {
    public BasicRenderer(String name, Render render, RegionCoordinate region) {
        super(name, render, region);
    }

    @Override
    public void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState state, BlockPos blockPos, BlockPos fluidPos, Biome biome, int x, int z, int[] lastY, int color) {
        // fix true block color
        int blockColor = Colors.fixBlockColor(getWorld(), getChunkHelper(), biome, state, blockPos, color);
        int pixelColor = blockColor == 0 ? blockColor : Colors.setAlpha(0xFF, blockColor);

        // work out the heightmap
        pixelColor = Colors.mix(pixelColor, scanHeightMap(blockPos, lastY, x));

        // fancy water, yum
        if (fluidPos != null && getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
            int fluidColor = fancyWater(blockPos, state, biome, (fluidPos.getY() - blockPos.getY()) * 0.025F);
            pixelColor = Colors.mix(pixelColor, fluidColor);
        }

        int pixelX = blockPos.getX() & Image.SIZE - 1;
        int pixelZ = blockPos.getZ() & Image.SIZE - 1;

        getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
    }
}
