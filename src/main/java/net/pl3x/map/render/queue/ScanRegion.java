package net.pl3x.map.render.queue;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
import net.pl3x.map.world.MapWorld;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;

public class ScanRegion implements Runnable {
    private final AbstractRender render;
    private final MapWorld mapWorld;
    private final RegionCoordinate region;
    private final ChunkHelper chunkHelper;

    public ScanRegion(AbstractRender render, RegionCoordinate region) {
        this.render = render;
        this.mapWorld = render.getWorld();
        this.region = region;
        this.chunkHelper = new ChunkHelper(render);
    }

    @Override
    public void run() {
        // wrap in try/catch because ExecutorService's Future swallows all exceptions :3
        try {
        justDoIt();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void justDoIt() {
        int regionX = this.region.getRegionX();
        int regionZ = this.region.getRegionZ();

        Logger.debug("[" + Thread.currentThread().getName() + "] Rendering region " + regionX + ", " + regionZ);

        ServerLevel level = this.mapWorld.getLevel();

        int minY = level.getMinBuildHeight();

        // allocate images
        Image.Set imageSet = new Image.Set(this.mapWorld, this.region);

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
                    this.render.getProgress().getProcessedChunks().incrementAndGet();
                    continue;
                }

                BlockState state;
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
                                int color;
                                boolean isFluid;
                                do {
                                    pos.move(Direction.DOWN);
                                    state = northChunk.getBlockState(pos);
                                    color = Colors.getBlockColor(this.mapWorld, null, state, pos);
                                    isFluid = !state.getFluidState().isEmpty();
                                } while (pos.getY() > minY && (color <= 0 || isFluid));
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
                        Biome biome = null;
                        int blockColor = 0;
                        boolean isFluid;
                        float depth = 0F;
                        Boolean lava = null;
                        do {
                            pos.move(Direction.DOWN);
                            state = chunk.getBlockState(pos);
                            isFluid = !state.getFluidState().isEmpty();
                            if (isFluid) {
                                if (lava == null) {
                                    lava = chunk.getBlockState(pos).is(Blocks.LAVA);
                                }
                                depth += 0.025F;
                            } else {
                                blockColor = Colors.getBlockColor(this.mapWorld, null, state, pos);
                            }
                        } while (pos.getY() > minY && (blockColor <= 0 || isFluid));

                        // biomes layer
                        if (this.render.renderBiomes()) {
                            Holder<Biome> biomeHolder = this.chunkHelper.getBiome(this.mapWorld, pos);
                            biome = biomeHolder.value();
                            imageSet.getBiomes().setPixel(pixelX, pixelZ, Colors.biomeColors.getOrDefault(biomeHolder.unwrapKey().orElse(null), 0));
                        }

                        // blocks layers
                        if (this.render.renderBlocks()) {
                            // recalculate grass/foliage in correct biome
                            if (biome != null && (Colors.isGrass(state.getBlock()) || Colors.isFoliage(state.getBlock()))) {
                                blockColor = Colors.getBlockColor(this.mapWorld, biome, state, pos);
                            }
                            imageSet.getBlocks().setPixel(pixelX, pixelZ, blockColor == 0 ? blockColor : (0xFF << 24) | blockColor);
                        }

                        // fluids layers
                        if (this.render.renderFluids()) {
                            lava = BooleanUtils.isTrue(lava);
                            int fluidColor = lava ? Colors.blockColors.get(Blocks.LAVA) : Colors.getWaterColor(this.mapWorld, biome);
                            // let's do some maths to get pretty fluid colors based on depth
                            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, lava ? 0.3F : 0.45F, Easing.cubicOut(depth / 1.5F)));
                            fluidColor = Colors.setAlpha(lava ? 0xFF : (int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
                            imageSet.getFluids().setPixel(pixelX, pixelZ, fluidColor);
                        }

                        // heights layers
                        if (this.render.renderHeights()) {
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
                }

                // increment for CPS counter
                this.render.getProgress().getProcessedChunks().incrementAndGet();

            }
        }

        // save images to disk
        if (!this.render.isCancelled()) {
            //ThreadManager.INSTANCE.getSaveExecutor().submit(() -> {
            //    try {
                    imageSet.save();
            //    } catch (Throwable t ) {
            //        t.printStackTrace();
            //    }
            //});
        }

        // we're done with this region \o/
        cleanup();
    }

    private void cleanup() {
        this.chunkHelper.clear();
        this.render.getProgress().getProcessedRegions().getAndIncrement();
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
