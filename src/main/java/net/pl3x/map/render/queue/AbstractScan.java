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
import net.pl3x.map.configuration.Advanced;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.render.AbstractRender;
import net.pl3x.map.render.Image;
import net.pl3x.map.render.iterator.coordinate.Coordinate;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.ChunkHelper;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.MapWorld;
import org.apache.commons.lang3.BooleanUtils;

import java.util.Arrays;

public abstract class AbstractScan implements Runnable {
    protected final AbstractRender render;
    protected final MapWorld mapWorld;
    private final ChunkHelper chunkHelper;

    private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
    protected ServerLevel level;
    protected WorldConfig config;
    private ChunkAccess chunk;
    private BlockState state;
    protected BiomeColors biomeColors;
    private Biome biome;
    protected int chunkX;
    protected int chunkZ;
    private int blockX, blockZ;
    private int pixelX, pixelZ;
    private int blockColor;
    protected int minY;
    private boolean isFluid;
    private Boolean lava;
    private float depth;
    private final int[] lastY = new int[16];

    protected Image.Set imageSet;

    public AbstractScan(AbstractRender render) {
        this.render = render;
        this.mapWorld = render.getWorld();
        this.chunkHelper = new ChunkHelper(render);
    }

    protected void scanChunk() {
        // make sure render task is still running
        if (this.render.isCancelled()) {
            cleanup();
            return;
        }

        while (this.mapWorld.isPaused()) {
            this.render.sleep(500);
        }

        chunk = this.chunkHelper.getChunk(level, chunkX, chunkZ);
        if (chunk == null) {
            return;
        }

        blockX = Coordinate.chunkToBlock(chunkX);
        blockZ = Coordinate.chunkToBlock(chunkZ);

        // iterate each block in this chunk
        for (int z = 0; z < 16; z++) {

            // we need the bottom row of the chunk to the north to get heightmap correct
            if (z == 0 && config.RENDER_LAYER_HEIGHTS) {
                scanNorthChunk();
            }

            for (int x = 0; x < 16; x++) {

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
                findFirstRenderableBlock();

                // biomes layer
                if (config.RENDER_LAYER_BIOMES) {
                    scanBiome();
                }

                // temperatures layer
                if (config.RENDER_LAYER_TEMPS) {
                    scanTemps();
                }

                // humidity layer
                if (config.RENDER_LAYER_HUMIDITY) {
                    scanHumidity();
                }

                // inhabited layer
                if (config.RENDER_LAYER_INHABITED) {
                    scanInhabited();
                }

                // blocks layers
                if (config.RENDER_LAYER_BLOCKS) {
                    scanBlock();
                }

                // fluids layers
                if (config.RENDER_LAYER_FLUIDS && config.RENDER_FLUIDS_TRANSLUCENT) {
                    scanFluid();
                }

                // heights layers
                if (config.RENDER_LAYER_HEIGHTS) {
                    scanHeightmap(x);
                }

            }
        }
    }

    protected void scanNorthChunk() {
        ChunkAccess northChunk = this.chunkHelper.getChunk(level, chunkX, chunkZ - 1);
        if (northChunk == null) {
            Arrays.fill(lastY, Integer.MAX_VALUE);
        } else {
            for (int x = 0; x < 16; x++) {
                pos.set(blockX + x, 0, blockZ + 15);
                pos.setY(northChunk.getHeight(Heightmap.Types.WORLD_SURFACE, pos.getX(), pos.getZ()) + 1);
                do {
                    pos.move(Direction.DOWN);
                    state = northChunk.getBlockState(pos);
                    blockColor = Colors.getBlockColor(state);
                    isFluid = config.RENDER_FLUIDS_TRANSLUCENT && !state.getFluidState().isEmpty();
                } while (pos.getY() > minY && (blockColor <= 0 || isFluid));
                lastY[x] = pos.getY();
            }
        }
    }

    protected void findFirstRenderableBlock() {
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
                blockColor = Colors.getBlockColor(state);
            }
        } while (pos.getY() > minY && (blockColor <= 0 || (isFluid && config.RENDER_FLUIDS_TRANSLUCENT)));
    }

    protected void scanBiome() {
        Holder<Biome> biomeHolder = getBiome();
        imageSet.getBiomes().setPixel(pixelX, pixelZ, Advanced.BIOME_COLORS.getOrDefault(biomeHolder.unwrapKey().orElse(null), 0));
    }

    protected void scanTemps() {
        if (biome == null) {
            getBiome();
        }
        @SuppressWarnings("deprecation")
        float temp = biome.getTemperature(pos);
        int rgb = Colors.mix(0xFF0000FF, 0xFFFF0000, Mathf.inverseLerp(-1F, 2F, temp));
        imageSet.getTemps().setPixel(pixelX, pixelZ, rgb);
    }

    protected void scanHumidity() {
        if (biome == null) {
            getBiome();
        }
        float humidity = biome.getDownfall();
        int rgb = Colors.mix(0xFFFFFFFF, 0xFF0000FF, Mathf.inverseLerp(0F, 1F, humidity));
        imageSet.getHumidity().setPixel(pixelX, pixelZ, rgb);
    }

    protected void scanInhabited() {
        long inhabited = chunk.getInhabitedTime();
        if (inhabited > mapWorld.highestInhabitedTime) {
            mapWorld.highestInhabitedTime = inhabited;
        }
        int rgb = Colors.mix(0xFF888888, 0xFF00FF00, Mathf.inverseLerp(0F, mapWorld.highestInhabitedTime, inhabited));
        imageSet.getInhabited().setPixel(pixelX, pixelZ, rgb);
    }

    protected void scanBlock() {
        if (biome != null) {
            // update color for correct biome
            blockColor = biomeColors.fixBiomeColor(this.chunkHelper, biome, state, pos, blockColor);
        }
        imageSet.getBlocks().setPixel(pixelX, pixelZ, blockColor == 0 ? blockColor : (0xFF << 24) | blockColor);
    }

    protected void scanFluid() {
        lava = BooleanUtils.isTrue(lava);
        int fluidColor = lava ? Colors.getBlockColor(Blocks.LAVA.defaultBlockState()) : biomeColors.getWaterColor(this.chunkHelper, biome, new BlockPos(pos.getX(), pos.getY() + (depth / 0.025F), pos.getZ()), config.RENDER_BLOCKS_BIOME_BLEND);
        // let's do some maths to get pretty fluid colors based on depth
        fluidColor = Colors.lerpARGB(fluidColor, 0xFF000000, Mathf.clamp(0, lava ? 0.3F : 0.45F, Easing.cubicOut(depth / 1.5F)));
        fluidColor = Colors.setAlpha(lava ? 0xFF : (int) (Easing.quinticOut(Mathf.clamp(0, 1, depth * 5F)) * 0xFF), fluidColor);
        imageSet.getFluids().setPixel(pixelX, pixelZ, fluidColor);
    }

    protected void scanHeightmap(int x) {
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

    protected void cleanup() {
        this.chunkHelper.clear();
        this.render.getProgress().getProcessedRegions().getAndIncrement();
    }

    private Holder<Biome> getBiome() {
        Holder<Biome> biomeHolder;
        if (config.RENDER_BLOCKS_BIOME_BLEND > 0) {
            biomeHolder = this.chunkHelper.getBiomeWithCaching(this.mapWorld, pos);
        } else {
            biomeHolder = this.chunkHelper.getBiome(this.mapWorld, pos);
        }
        biome = biomeHolder.value();
        return biomeHolder;
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
