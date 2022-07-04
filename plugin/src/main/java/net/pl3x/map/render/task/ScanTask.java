package net.pl3x.map.render.task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.Heightmap;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.Coordinate;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.ChunkHelper;
import net.pl3x.map.world.MapWorld;

public class ScanTask implements Runnable {
    private final Render render;
    private final RegionCoordinate region;
    private final Area area;

    private final MapWorld mapWorld;
    private final ChunkHelper chunkHelper;

    private final List<Renderer> renderers = new ArrayList<>();

    public ScanTask(Render render, RegionCoordinate region, Area area) {
        this.render = render;
        this.region = region;
        this.area = area;

        this.mapWorld = render.getWorld();
        this.chunkHelper = new ChunkHelper(render);

        getWorld().getConfig().RENDER_SCANNERS.forEach(name -> {
            Renderer renderer = Renderers.INSTANCE.createRenderer(name, getRender(), getRegion());
            if (renderer != null) {
                this.renderers.add(renderer);
            }
        });
    }

    public Render getRender() {
        return this.render;
    }

    public RegionCoordinate getRegion() {
        return this.region;
    }

    public Area getScannableArea() {
        return this.area;
    }

    public MapWorld getWorld() {
        return this.mapWorld;
    }

    public ChunkHelper getChunkHelper() {
        return this.chunkHelper;
    }

    @Override
    public void run() {
        // wrap in try/catch because executor swallows all exceptions :3
        try {
            scanRegion();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void scanRegion() {
        // allocate images
        this.renderers.forEach(renderer -> renderer.setImageHolder(new Image.Holder(renderer.getName(), getWorld(), getRegion())));

        // scan chunks in region
        for (int chunkX = getRegion().getChunkX(); chunkX < getRegion().getChunkX() + 32; chunkX++) {
            for (int chunkZ = getRegion().getChunkZ(); chunkZ < getRegion().getChunkZ() + 32; chunkZ++) {
                // make sure render task is still running
                if (getRender().isCancelled()) {
                    // don't forget to clean up before exiting
                    return;
                }

                // make sure we're allowed to scan this chunk
                if (!getScannableArea().contains(chunkX, chunkZ)) {
                    return;
                }

                // pause here if we have to
                while (getWorld().isPaused()) {
                    getRender().sleep(500);
                }

                // scan the chunk
                scanChunk(chunkX, chunkZ);

                // we're done with this chunk \o/
                getRender().getProgress().getProcessedChunks().incrementAndGet();
            }
        }

        // save images to disk
        if (!getRender().isCancelled()) {
            // submit to IO executor, so we can move on to next region without waiting
            getRender().getImageExecutor().submit(() -> {
                // surround in try/catch because executor eats exceptions
                try {
                    this.renderers.forEach(renderer -> renderer.getImageHolder().save());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        // we're done with this region \o/
        getRender().getProgress().getProcessedRegions().getAndIncrement();
    }

    public void scanChunk(int chunkX, int chunkZ) {
        ChunkAccess chunk = getChunkHelper().getChunk(getWorld().getLevel(), chunkX, chunkZ);
        if (chunk == null) {
            return;
        }

        int blockX = Coordinate.chunkToBlock(chunkX);
        int blockZ = Coordinate.chunkToBlock(chunkZ);

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        int[] lastY = new int[16];

        // iterate each block in this chunk
        for (int z = 0; z < 16; z++) {

            // we need the bottom row of the chunk to the north to get heightmap correct
            if (z == 0) {
                scanNorthChunk(chunkX, chunkZ, blockX, blockZ, blockPos, lastY);
            }

            for (int x = 0; x < 16; x++) {

                // find our starting point
                blockPos.set(blockX + x, 0, blockZ + z);
                blockPos.setY(chunk.getHeight(Heightmap.Types.WORLD_SURFACE, blockPos.getX(), blockPos.getZ()) + 1);

                scanBlock(chunk, blockPos, lastY, x, z);
            }
        }
    }

    public void scanBlock(ChunkAccess chunk, BlockPos.MutableBlockPos blockPos, int[] lastY, int x, int z) {
        // determine the biome
        Biome biome = scanBiome(blockPos).value();

        // iterate down until we find a renderable block
        List<Integer> glass = new ArrayList<>();
        BlockPos fluidPos = null;
        BlockState state;
        int blockColor = 0;
        do {
            blockPos.move(Direction.DOWN);
            state = chunk.getBlockState(blockPos);
            if (!state.getFluidState().isEmpty()) {
                if (fluidPos == null) {
                    fluidPos = blockPos.mutable();
                }
                continue;
            }
            // just get a quick color for now
            blockColor = Colors.getRawBlockColor(state);
            if (getWorld().getConfig().RENDER_TRANSLUCENT_GLASS && isGlass(state)) {
                glass.add(blockColor);
                continue;
            }
            if (blockColor > 0) {
                break;
            }
        } while (blockPos.getY() > getWorld().getLevel().getMinBuildHeight());

        for (Renderer renderer : this.renderers) {
            renderer.doIt(getWorld(), chunk, state, blockPos, fluidPos, biome, x, z, glass, lastY, blockColor);
        }
    }

    public Holder<Biome> scanBiome(BlockPos pos) {
        if (getWorld().getConfig().RENDER_BIOME_BLEND > 0) {
            return getChunkHelper().getBiomeWithCaching(getWorld(), pos);
        } else {
            return getChunkHelper().getBiome(getWorld(), pos);
        }
    }

    public void scanNorthChunk(int chunkX, int chunkZ, int blockX, int blockZ, BlockPos.MutableBlockPos blockPos, int[] lastY) {
        ChunkAccess northChunk = getChunkHelper().getChunk(getWorld().getLevel(), chunkX, chunkZ - 1);
        if (northChunk == null) {
            Arrays.fill(lastY, Integer.MAX_VALUE);
        } else {
            for (int x = 0; x < 16; x++) {
                blockPos.set(blockX + x, 0, blockZ + 15);
                blockPos.setY(northChunk.getHeight(Heightmap.Types.WORLD_SURFACE, blockPos.getX(), blockPos.getZ()) + 1);
                do {
                    blockPos.move(Direction.DOWN);
                    BlockState state = northChunk.getBlockState(blockPos);
                    if (!state.getFluidState().isEmpty()) {
                        continue;
                    }
                    if (getWorld().getConfig().RENDER_TRANSLUCENT_GLASS && isGlass(state)) {
                        continue;
                    }
                    if (Colors.getRawBlockColor(state) > 0) {
                        break;
                    }
                } while (blockPos.getY() > getWorld().getLevel().getMinBuildHeight());
                lastY[x] = blockPos.getY();
            }
        }
    }

    public static boolean isGlass(BlockState state) {
        return state.is(Blocks.GLASS) || state.is(Blocks.GLASS_PANE) ||
                state.getBlock() instanceof StainedGlassBlock ||
                state.getBlock() instanceof StainedGlassPaneBlock;
    }
}
