package net.pl3x.map.addon;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.render.heightmap.Heightmap;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderer;
import net.pl3x.map.render.task.Renderers;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.MapWorld;
import org.bukkit.plugin.java.JavaPlugin;

public class InhabitedRenderer extends JavaPlugin {
    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.register("inhabited", InhabitedScanner.class);
    }

    @Override
    public void onDisable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.unregister("inhabited");
    }

    public static class InhabitedScanner extends Renderer {
        public InhabitedScanner(String name, Render render, RegionCoordinate region) {
            super(name, render, region);
        }

        @Override
        public void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState blockState, BlockPos blockPos, Biome blockBiome, BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, int x, int z, List<Integer> glass, Heightmap heightmap, int color) {
            // copied from BasicRenderer

            // fix true block color
            boolean flatWater = fluidPos != null && !getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS;
            int blockColor;
            if (flatWater) {
                blockColor = Colors.fixBlockColor(getRender().getBiomeColors(), getChunkHelper(), fluidBiome, fluidState, fluidPos, color);
            } else {
                blockColor = Colors.fixBlockColor(getRender().getBiomeColors(), getChunkHelper(), blockBiome, blockState, blockPos, color);
            }
            int pixelColor = blockColor == 0 ? blockColor : Colors.setAlpha(0xFF, blockColor);

            // work out the heightmap
            int heightmapColor = heightmap.scan(blockPos, x, z, flatWater);
            pixelColor = Colors.mix(pixelColor, heightmapColor);

            // fancy fluids, yum
            if (fluidPos != null && getWorld().getConfig().RENDER_TRANSLUCENT_FLUIDS) {
                int fluidColor = fancyFluids(fluidState, fluidPos, fluidBiome, (fluidPos.getY() - blockPos.getY()) * 0.025F);
                pixelColor = Colors.mix(pixelColor, fluidColor);
            }

            // now we can overlay the basic render

            // we hsb lerp between blue and red with ratio being the
            // percent inhabited time is of the maxed out inhabited time
            float ratio = Mathf.inverseLerp(0F, 3600000, chunk.getInhabitedTime());
            int inhabitedRGB = Colors.lerpHSB(0xFF0000FF, 0xFFFF0000, ratio, false);

            // set the color, mixing our heatmap on top
            // set a low enough alpha, so we can see the basic map underneath
            pixelColor = Colors.mix(pixelColor, Colors.setAlpha(0x88, inhabitedRGB));

            int pixelX = blockPos.getX() & Image.SIZE - 1;
            int pixelZ = blockPos.getZ() & Image.SIZE - 1;

            getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
        }
    }
}
