package net.pl3x.map.render.task.builtin;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.LightEngine;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.MapWorld;

public class BasicRenderer extends Renderer {
    public BasicRenderer(String name, Render render, RegionCoordinate region) {
        super(name, render, region);
    }

    @Override
    public void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState blockState, BlockPos blockPos, Biome blockBiome, BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, int x, int z, List<Integer> glass, int[] lastY, int color) {
        // fix true block color
        int blockColor = Colors.fixBlockColor(getWorld(), getChunkHelper(), blockBiome, blockState, blockPos, color);
        int pixelColor = blockColor == 0 ? blockColor : Colors.setAlpha(0xFF, blockColor);

        // work out the heightmap
        pixelColor = Colors.mix(pixelColor, scanHeightMap(blockPos, lastY, x));

        // fancy fluids, yum
        if (fluidPos != null && getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
            int fluidColor = fancyFluids(fluidState, fluidPos, fluidBiome, (fluidPos.getY() - blockPos.getY()) * 0.025F);
            pixelColor = Colors.mix(pixelColor, fluidColor);
        }

        // if there was translucent glass, mix it in here
        if (!glass.isEmpty()) {
            pixelColor = Colors.mix(pixelColor, Colors.merge(glass), Math.min(1.0F, 0.70F + (0.05F * glass.size())));
        }

        if (getWorld().getConfig().RENDER_SKYLIGHT > -1 && getWorld().getConfig().RENDER_SKYLIGHT < 15) {
            // get light level right above this block
            int blockLight;
            if (fluidState.is(Blocks.LAVA)) {
                // not sure why lava isn't returning
                // the correct light levels in the nether..
                // maybe a starlight optimization?
                blockLight = 15;
            } else {
                blockLight = LightEngine.getBlockLightValue(chunk, (fluidPos == null ? blockPos : fluidPos).above());
            }
            // blocklight in 0-255 range
            int alpha = (int) (Mathf.inverseLerp(0, 15, blockLight) * 0xFF);
            // skylight level in 0-15 range
            int skylight = (int) Mathf.clamp(0, 15, getWorld().getConfig().RENDER_SKYLIGHT);
            // inverse of the skylight level in 0-1 range
            float inverseSkylight = Mathf.inverseLerp(0, 15, 15 - skylight);
            // how much darkness to draw in 0-255 range
            int darkness = (int) Mathf.clamp(0, 0xFF, (0xFF * inverseSkylight) - alpha);
            // mix it into the pixel
            pixelColor = Colors.mix(pixelColor, Colors.setAlpha(darkness, 0x00));
        }

        int pixelX = blockPos.getX() & Image.SIZE - 1;
        int pixelZ = blockPos.getZ() & Image.SIZE - 1;

        getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
    }
}
