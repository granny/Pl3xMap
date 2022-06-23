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
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.Image;
import net.pl3x.map.render.iterator.coordinate.Coordinate;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.render.task.ThreadManager;
import net.pl3x.map.util.BiomeColors;
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
        // reusable vars
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        ChunkAccess chunk, northChunk;
        BlockState state;
        Holder<Biome> biomeHolder;
        Biome biome;
        int chunkX, chunkZ, blockX, blockZ, pixelX, pixelZ, x, z;
        int blockColor, fluidColor, heightColor;
        boolean isFluid;
        Boolean lava;
        float depth;
        int[] lastY = new int[16];

        int regionX = this.region.getRegionX();
        int regionZ = this.region.getRegionZ();

        Logger.debug("[" + Thread.currentThread().getName() + "] Rendering region " + regionX + ", " + regionZ);

        WorldConfig config = this.mapWorld.getConfig();
        ServerLevel level = this.mapWorld.getLevel();
        BiomeColors biomeColors = this.mapWorld.getBiomeColors();
        int minY = level.getMinBuildHeight();

        // allocate images
        Image.Set imageSet = new Image.Set(this.mapWorld, this.region);

        // scan chunks in region
        for (chunkX = this.region.getChunkX(); chunkX < this.region.getChunkX() + 32; chunkX++) {
            for (chunkZ = this.region.getChunkZ(); chunkZ < this.region.getChunkZ() + 32; chunkZ++) {

                // make sure render task is still running
                if (this.render.isCancelled()) {
                    cleanup();
                    return;
                }

                chunk = this.chunkHelper.getChunk(level, chunkX, chunkZ);
                if (chunk == null) {
                    // increment for CPS counter
                    this.render.getProgress().getProcessedChunks().incrementAndGet();
                    continue;
                }

                blockX = Coordinate.chunkToBlock(chunkX);
                blockZ = Coordinate.chunkToBlock(chunkZ);

                // iterate each block in this chunk
                for (z = 0; z < 16; z++) {

                    // we need the bottom row of the chunk to the north to get heightmap correct
                    if (z == 0 && config.RENDER_LAYER_HEIGHTS) {
                        northChunk = this.chunkHelper.getChunk(level, chunkX, chunkZ - 1);
                        if (northChunk == null) {
                            Arrays.fill(lastY, Integer.MAX_VALUE);
                        } else {
                            for (x = 0; x < 16; x++) {
                                pos.set(blockX + x, 0, blockZ + 15);
                                pos.setY(northChunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 1);
                                do {
                                    pos.move(Direction.DOWN);
                                    state = northChunk.getBlockState(pos);
                                    blockColor = Colors.getBlockColor(this.mapWorld, state, pos);
                                    isFluid = config.RENDER_FLUIDS_TRANSLUCENT && !state.getFluidState().isEmpty();
                                } while (pos.getY() > minY && (blockColor <= 0 || isFluid));
                                lastY[x] = pos.getY();
                            }
                        }
                    }

                    for (x = 0; x < 16; x++) {

                        // find our starting point
                        pos.set(blockX + x, 0, blockZ + z);
                        pos.setY(chunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 1);

                        // setup data
                        pixelX = pos.getX() & Image.SIZE - 1;
                        pixelZ = pos.getZ() & Image.SIZE - 1;
                        biome = null;
                        blockColor = 0;
                        depth = 0F;
                        lava = null;

                        // let's find the right block to work with
                        do {
                            pos.move(Direction.DOWN);
                            state = chunk.getBlockState(pos);
                            isFluid = !state.getFluidState().isEmpty();
                            if (isFluid && config.RENDER_FLUIDS_TRANSLUCENT) {
                                if (lava == null) {
                                    lava = chunk.getBlockState(pos).is(Blocks.LAVA);
                                }
                                depth += 0.025F;
                            } else {
                                blockColor = Colors.getBlockColor(this.mapWorld, state, pos);
                            }
                        } while (pos.getY() > minY && (blockColor <= 0 || (isFluid && config.RENDER_FLUIDS_TRANSLUCENT)));

                        // biomes layer
                        if (config.RENDER_LAYER_BIOMES) {
                            if (config.RENDER_BLOCKS_BIOME_BLEND > 0) {
                                biomeHolder = this.chunkHelper.getBiomeWithCaching(this.mapWorld, pos);
                            } else {
                                biomeHolder = this.chunkHelper.getBiome(this.mapWorld, pos);
                            }
                            biome = biomeHolder.value();
                            imageSet.getBiomes().setPixel(pixelX, pixelZ, Colors.biomeColors.getOrDefault(biomeHolder.unwrapKey().orElse(null), 0));
                        }

                        // blocks layers
                        if (config.RENDER_LAYER_BLOCKS) {
                            if (biome != null) {
                                // update color for correct biome
                                blockColor = biomeColors.fixBiomeColor(this.chunkHelper, biome, state, pos, blockColor);
                            }
                            imageSet.getBlocks().setPixel(pixelX, pixelZ, blockColor == 0 ? blockColor : (0xFF << 24) | blockColor);
                        }

                        // fluids layers
                        if (config.RENDER_LAYER_FLUIDS && config.RENDER_FLUIDS_TRANSLUCENT) {
                            lava = BooleanUtils.isTrue(lava);
                            fluidColor = lava ? Colors.blockColors.get(Blocks.LAVA) : biomeColors.getWaterColor(this.chunkHelper, biome, new BlockPos(pos.getX(), pos.getY() + (depth / 0.025F), pos.getZ()), config.RENDER_BLOCKS_BIOME_BLEND);
                            // let's do some maths to get pretty fluid colors based on depth
                            fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, lava ? 0.3F : 0.45F, Easing.cubicOut(depth / 1.5F)));
                            fluidColor = Colors.setAlpha(lava ? 0xFF : (int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
                            imageSet.getFluids().setPixel(pixelX, pixelZ, fluidColor);
                        }

                        // heights layers
                        if (config.RENDER_LAYER_HEIGHTS) {
                            heightColor = 0x22;
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
            ThreadManager.INSTANCE.getSaveExecutor().submit(() -> {
                try {
                    imageSet.save();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
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
