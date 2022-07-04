package net.pl3x.map.addon;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.configuration.Advanced;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.Renderers;
import net.pl3x.map.render.task.builtin.BasicRenderer;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.MapWorld;
import org.bukkit.plugin.java.JavaPlugin;

public class BiomeRenderer extends JavaPlugin {
    @Override
    public void onEnable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.register("biomes", BiomeScanner.class);
    }

    @Override
    public void onDisable() {
        // register our custom renderer with Pl3xMap
        Renderers.INSTANCE.unregister("biomes");
    }

    public static class BiomeScanner extends BasicRenderer {
        public BiomeScanner(String name, Render render, RegionCoordinate region) {
            super(name, render, region);
        }

        @Override
        public void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState state, BlockPos blockPos, BlockPos fluidPos, Biome biome, int x, int z, List<Integer> glass, int[] lastY, int color) {
            // simplified from BasicRenderer

            // determine the biome
            ResourceKey<Biome> biomeKey = BiomeColors.getBiomeRegistry(mapWorld.getLevel()).getResourceKey(biome).orElse(null);
            int pixelColor = biomeKey == null ? 0 : Advanced.BIOME_COLORS.getOrDefault(biomeKey, 0);

            // work out the heightmap
            pixelColor = Colors.mix(pixelColor, scanHeightMap(blockPos, lastY, x) << 24);

            int pixelX = blockPos.getX() & Image.SIZE - 1;
            int pixelZ = blockPos.getZ() & Image.SIZE - 1;

            getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);
        }
    }
}
