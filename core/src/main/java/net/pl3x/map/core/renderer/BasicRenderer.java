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
    public void allocateData(World world, Point region) {
        super.allocateData(world, region);
        this.lightImage = new TileImage("light", world, region);
    }

    @Override
    public void saveData() {
        super.saveData();
        this.lightImage.saveToDisk();
    }

    @Override
    public void scanData(Region region) {
        int cX = region.getX() << 5;
        int cZ = region.getZ() << 5;

        // iterate each chunk in this region
        for (int chunkX = cX; chunkX < cX + 32; chunkX++) {
            int bX = chunkX << 4;
            for (int chunkZ = cZ; chunkZ < cZ + 32; chunkZ++) {
                int bZ = chunkZ << 4;
                Chunk chunk = region.getChunk(chunkX, chunkZ);
                // iterate each block in this chunk
                for (int blockX = bX; blockX < bX + 16; blockX++) {
                    for (int blockZ = bZ; blockZ < bZ + 16; blockZ++) {
                        Chunk.BlockData data = chunk.getData(blockX, blockZ);
                        if (data == null) {
                            // this shouldn't happen, but just in case...
                            continue;
                        }

                        int pixelColor = basicPixelColor(region, data.getBlockState(), data.getFluidState(), data.getBiome(region, blockX, blockZ), blockX, data.getBlockY(), blockZ, data.getFluidY());
                        getTileImage().setPixel(blockX, blockZ, pixelColor);

                        // get light level right above this block
                        int lightPixel = calculateLight(chunk, data.getFluidState(), blockX, data.getBlockY(), blockZ, data.getFluidY(), pixelColor);
                        this.lightImage.setPixel(blockX, blockZ, lightPixel);
                    }
                }
            }
        }
    }
}
