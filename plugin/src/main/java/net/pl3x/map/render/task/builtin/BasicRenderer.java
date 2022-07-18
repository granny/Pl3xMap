package net.pl3x.map.render.task.builtin;

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
import net.pl3x.map.world.MapWorld;

public final class BasicRenderer extends Renderer {
    private Image.Holder lightImageHolder;

    public BasicRenderer(String name, Render render, RegionCoordinate region) {
        super(name, render, region);
    }

    @Override
    public void allocateImages() {
        super.allocateImages();
        this.lightImageHolder = new Image.Holder("light", getWorld(), getRegion());
    }

    @Override
    public void saveImages() {
        super.saveImages();
        this.lightImageHolder.save();
    }

    @Override
    public void doIt(MapWorld mapWorld, ChunkAccess chunk, BlockState blockState, BlockPos blockPos, Biome blockBiome, BlockState fluidState, BlockPos fluidPos, Biome fluidBiome, int x, int z, List<Integer> glass, Heightmap heightmap, int color) {
        // get basic pixel color
        int pixelColor = basicPixelColor(blockState, blockPos, blockBiome, fluidState, fluidPos, fluidBiome, x, z, glass, heightmap, color);

        int pixelX = blockPos.getX() & Image.SIZE - 1;
        int pixelZ = blockPos.getZ() & Image.SIZE - 1;

        getImageHolder().getImage().setPixel(pixelX, pixelZ, pixelColor);

        // get light level right above this block
        int lightPixel = calculateLight(chunk, blockPos, fluidState, fluidPos, pixelColor);
        this.lightImageHolder.getImage().setPixel(pixelX, pixelZ, lightPixel);
    }
}
