package net.pl3x.map.core.renderer;

import net.pl3x.map.core.image.TileImage;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.world.Chunk;
import net.pl3x.map.core.world.Region;
import net.pl3x.map.core.world.World;

public final class BasicRenderer extends Renderer {
    private TileImage lightImage;

    public BasicRenderer(World world, Builder builder) {
        super(world, builder);
    }

    @Override
    public void allocateData(Point region) {
        super.allocateData(region);
        this.lightImage = new TileImage("light", getWorld(), region);
    }

    @Override
    public void saveData(Point region) {
        super.saveData(region);
        this.lightImage.saveToDisk();
    }

    @Override
    public void scanBlock(Region region, Chunk chunk, Chunk.BlockData data, int blockX, int blockZ) {
        int pixelColor = basicPixelColor(region, data.getBlockState(), data.getFluidState(), data.getBiome(region, blockX, blockZ), blockX, data.getBlockY(), blockZ, data.getFluidY());
        getTileImage().setPixel(blockX, blockZ, pixelColor);

        // get light level right above this block
        int lightPixel = calculateLight(chunk, data.getFluidState(), blockX, data.getBlockY(), blockZ, data.getFluidY(), pixelColor);
        this.lightImage.setPixel(blockX, blockZ, lightPixel);
    }
}
