package net.pl3x.map.render.queue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.Image;
import net.pl3x.map.render.iterator.coordinate.Coordinate;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.render.task.ThreadManager;
import net.pl3x.map.util.ChunkHelper;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;

import java.util.Arrays;

public class ScanRegion implements Runnable {
    private final AbstractRender render;
    private final RegionCoordinate region;
    private final ChunkHelper chunkHelper;

    private BlockPos.MutableBlockPos fluidPos = null;
    private BlockState state;
    private int minY;

    public ScanRegion(AbstractRender render, RegionCoordinate region) {
        this.render = render;
        this.region = region;
        this.chunkHelper = new ChunkHelper(render);
    }

    @Override
    public void run() {
        int regionX = this.region.getRegionX();
        int regionZ = this.region.getRegionZ();

        Logger.debug("[" + Thread.currentThread().getName() + "] Rendering region " + regionX + ", " + regionZ);

        ServerLevel level = this.render.getWorld().getLevel();
        this.minY = level.getMinBuildHeight();

        // allocate images
        Image.Set imageSet = new Image.Set(this.render, this.region);

        // scan chunks in region
        for (int chunkX = this.region.getChunkX(); chunkX < this.region.getChunkX() + 32; chunkX++) {
            for (int chunkZ = this.region.getChunkZ(); chunkZ < this.region.getChunkZ() + 32; chunkZ++) {

                // make sure render task is still running
                if (this.render.isCancelled()) {
                    cleanup();
                    return;
                }

                ChunkAccess chunk = this.chunkHelper.getChunk(level, chunkX, chunkZ);
                if (chunk == null) {
                    // increment for CPS counter
                    this.render.finishedChunks.incrementAndGet();
                    continue;
                }

                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
                int blockX = Coordinate.chunkToBlock(chunkX);
                int blockZ = Coordinate.chunkToBlock(chunkZ);
                int[] lastY = new int[16];

                // iterate each block in this chunk
                for (int z = 0; z < 16; z++) {

                    // we need the bottom row of the chunk to the north to get heightmap correct
                    if (z == 0 && this.render.renderHeights()) {
                        ChunkAccess northChunk = this.chunkHelper.getChunk(level, chunkX, chunkZ - 1);
                        if (northChunk == null) {
                            Arrays.fill(lastY, Integer.MAX_VALUE);
                        } else {
                            for (int x = 0; x < 16; x++) {
                                pos.set(blockX + x, 0, blockZ + 15);
                                pos.setY(northChunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 1);
                                findRenderableBlock(northChunk, pos);
                                lastY[x] = pos.getY();
                            }
                        }
                    }

                    for (int x = 0; x < 16; x++) {

                        // setup data
                        pos.set(blockX + x, 0, blockZ + z);
                        pos.setY(chunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 1);
                        int pixelX = pos.getX() & Image.SIZE - 1;
                        int pixelZ = pos.getZ() & Image.SIZE - 1;
                        this.fluidPos = null;
                        ResourceKey<Biome> biomeKey = null;
                        Biome biome = null;

                        // blocks, biomes, and heights layers
                        {
                            int blockColor = findRenderableBlock(chunk, pos);
                            // get biome at correct Y level
                            if (this.render.renderBlocks() || this.render.renderBiomes() || this.render.renderFluids()) {
                                Holder<Biome> biomeHolder = this.chunkHelper.getBiome(this.render.getWorld(), pos);
                                biomeKey = biomeHolder.unwrapKey().orElse(null);
                                biome = biomeHolder.value();
                            }
                            if (this.render.renderBlocks()) {
                                // recalculate grass/foliage in correct biome
                                if (Colors.isGrass(this.state.getBlock()) || Colors.isFoliage(this.state.getBlock())) {
                                    blockColor = Colors.getBlockColor(this.render.getWorld(), biome, this.state, pos);
                                }
                                // store pixel data
                                imageSet.getBlocks().setPixel(pixelX, pixelZ, blockColor == 0 ? blockColor : (0xFF << 24) | blockColor);
                            }
                            if (this.render.renderBiomes()) {
                                imageSet.getBiomes().setPixel(pixelX, pixelZ, Colors.biomeColors.getOrDefault(biomeKey, 0));
                            }
                            if (this.render.renderHeights()) {
                                // calculate height
                                int heightColor = 0x22;
                                if (lastY[x] != Integer.MAX_VALUE) {
                                    if (pos.getY() > lastY[x]) {
                                        heightColor = 0x00;
                                    } else if (pos.getY() < lastY[x]) {
                                        heightColor = 0x44;
                                    }
                                }
                                imageSet.getHeights().setPixel(pixelX, pixelZ, heightColor << 24);
                                lastY[x] = pos.getY();
                            }
                        }

                        // lights layer
                        /*if (this.render.renderLights()) {
                            // get block's light level
                            int light = level.getBrightness(LightLayer.BLOCK, (this.fluidPos == null ? pos : this.fluidPos).above());
                            // convert to alpha value
                            int alpha = (int) (Mathf.inverseLerp(0, 15, light) * 0xFF);
                            // convert to darkness color
                            int darkness = (int) Mathf.clamp(0x00, 0xFF, 0xBB - alpha) << 24;
                            imageSet.getLights().setPixel(pixelX, pixelZ, darkness);
                        }*/

                        // fluids layer
                        if (this.render.renderFluids()) {
                            int fluidColor = 0;
                            if (this.fluidPos != null) {
                                // setup initial fluid stuff
                                boolean lava;
                                if (chunk.getBlockState(this.fluidPos).is(Blocks.LAVA)) {
                                    lava = true;
                                    fluidColor = Colors.blockColors.get(Blocks.LAVA);
                                } else {
                                    lava = false;
                                    fluidColor = Colors.getWaterColor(this.render.getWorld(), biome);
                                }
                                // iterate down until we don't find any more fluids
                                float depth = 0F;
                                do {
                                    this.fluidPos.move(Direction.DOWN);
                                    this.state = chunk.getBlockState(this.fluidPos);
                                    depth += 0.025F;
                                } while (this.fluidPos.getY() > this.minY && !this.state.getFluidState().isEmpty());
                                // let's do some maths to get pretty fluid colors based on depth
                                fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, lava ? 0.3F : 0.45F, Easing.cubicOut(depth / 1.5F)));
                                fluidColor = Colors.setAlpha(lava ? 0xFF : (int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
                            }
                            imageSet.getFluids().setPixel(pixelX, pixelZ, fluidColor);
                        }

                    }
                }

                // increment for CPS counter
                this.render.finishedChunks.incrementAndGet();

            }
        }

        // save images to disk
        if (!this.render.isCancelled()) {
            ThreadManager.INSTANCE.getSaveExecutor().submit(imageSet::save);
        }

        // we're done with this region \o/
        cleanup();
    }

    private void cleanup() {
        this.chunkHelper.clear();
    }

    private int findRenderableBlock(ChunkAccess chunk, BlockPos.MutableBlockPos pos) {
        int color;
        boolean isFluid;
        // iterate downwards until we find a block to render
        do {
            pos.move(Direction.DOWN);
            this.state = chunk.getBlockState(pos);
            color = Colors.getBlockColor(this.render.getWorld(), null, this.state, pos);
            isFluid = this.state.getFluidState().isEmpty();
            // only track the first time we see a liquid
            if (this.fluidPos == null && !isFluid) {
                this.fluidPos = pos.mutable();
            }
        } while (pos.getY() > this.minY && (color <= 0 || !isFluid));
        return color;
    }

    public static class Easing {
        public static float cubicOut(float t) {
            return 1F + ((t -= 1F) * t * t);
        }

        public static float quinticOut(float t) {
            return 1F + ((t -= 1F) * t * t * t * t);
        }
    }
}
