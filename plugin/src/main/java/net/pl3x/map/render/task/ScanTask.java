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
import net.pl3x.map.render.Area;
import net.pl3x.map.render.Heightmap;
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
                    // don't forget to increment chunk counter
                    getRender().getProgress().getProcessedChunks().getAndIncrement();
                    return;
                }

                // make sure we're allowed to scan this chunk
                if (!getScannableArea().containsChunk(chunkX, chunkZ)) {
                    // don't forget to increment chunk counter
                    getRender().getProgress().getProcessedChunks().getAndIncrement();
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

        // world coordinates for most northwest block in chunk
        int blockX = Coordinate.chunkToBlock(chunkX);
        int blockZ = Coordinate.chunkToBlock(chunkZ);

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        Heightmap heightmap = new Heightmap();

        // iterate each block in this chunk
        for (int z = 0; z < 16; z++) {

            // we need the bottom row of the chunk to the north to get heightmap correct
            if (z == 0) {
                scanNorthChunk(chunkX, chunkZ, blockX, blockZ, blockPos, heightmap);
            }

            for (int x = 0; x < 16; x++) {

                // we need the right row of the chunk to the west to get heightmap correct
                if (x == 0) {
                    scanWestChunk(chunkX, chunkZ, blockX, blockZ, blockPos, heightmap);
                }

                // find our starting point
                blockPos.set(blockX + x, 0, blockZ + z);
                blockPos.setY(chunk.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, blockPos.getX(), blockPos.getZ()) + 1);

                // scan the block
                scanBlock(chunk, blockPos, heightmap, x, z);
            }
        }
    }

    public void scanBlock(ChunkAccess chunk, BlockPos.MutableBlockPos blockPos, Heightmap heightmap, int x, int z) {
        // determine the biome
        Biome blockBiome = scanBiome(blockPos).value();

        // if world has ceiling iterate down until we find air
        BlockState blockState;
        if (getWorld().getLevel().dimensionType().hasCeiling()) {
            do {
                blockPos.move(Direction.DOWN);
                blockState = chunk.getBlockState(blockPos);
            } while (blockPos.getY() > getWorld().getLevel().getMinBuildHeight() && !blockState.isAir());
        }

        // iterate down until we find a renderable block
        List<Integer> glass = new ArrayList<>();
        BlockPos fluidPos = null;
        int blockColor = 0;
        do {
            blockPos.move(Direction.DOWN);
            blockState = chunk.getBlockState(blockPos);
            if (!blockState.getFluidState().isEmpty()) {
                if (fluidPos == null) {
                    fluidPos = blockPos.mutable();
                }
                continue;
            }
            // just get a quick color for now
            blockColor = Colors.getRawBlockColor(blockState);
            if (getWorld().getConfig().RENDER_TRANSLUCENT_GLASS && isGlass(blockState)) {
                glass.add(blockColor);
                continue;
            }
            if (blockColor > 0) {
                break;
            }
        } while (blockPos.getY() > getWorld().getLevel().getMinBuildHeight());

        BlockState fluidState = null;
        Biome fluidBiome = null;
        if (fluidPos != null) {
            fluidState = chunk.getBlockState(fluidPos);
            fluidBiome = getChunkHelper().getBiomeWithCaching(getWorld(), fluidPos).value();
        }

        for (Renderer renderer : this.renderers) {
            renderer.doIt(getWorld(), chunk, blockState, blockPos, blockBiome, fluidState, fluidPos, fluidBiome, x, z, glass, heightmap, blockColor);
        }
    }

    public void scanNorthChunk(int chunkX, int chunkZ, int blockX, int blockZ, BlockPos.MutableBlockPos blockPos, Heightmap heightmap) {
        ChunkAccess northChunk = getChunkHelper().getChunk(getWorld().getLevel(), chunkX, chunkZ - 1);
        if (northChunk == null) {
            Arrays.fill(heightmap.x, Integer.MAX_VALUE);
        } else {
            for (int x = 0; x < 16; x++) {
                blockPos.set(blockX + x, 0, blockZ + 15);
                scanHeightmap(northChunk, blockPos);
                heightmap.x[x] = blockPos.getY();
            }
        }
    }

    public void scanWestChunk(int chunkX, int chunkZ, int blockX, int blockZ, BlockPos.MutableBlockPos blockPos, Heightmap heightmap) {
        ChunkAccess westChunk = getChunkHelper().getChunk(getWorld().getLevel(), chunkX - 1, chunkZ);
        if (westChunk == null) {
            Arrays.fill(heightmap.z, Integer.MAX_VALUE);
        } else {
            for (int z = 0; z < 16; z++) {
                blockPos.set(blockX + 15, 0, blockZ + z);
                scanHeightmap(westChunk, blockPos);
                heightmap.z[z] = blockPos.getY();
            }
        }
    }

    public void scanHeightmap(ChunkAccess chunkAccess, BlockPos.MutableBlockPos blockPos) {
        blockPos.setY(chunkAccess.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE, blockPos.getX(), blockPos.getZ()) + 1);
        // if world has ceiling iterate down until we find air
        BlockState state;
        if (getWorld().getLevel().dimensionType().hasCeiling()) {
            do {
                blockPos.move(Direction.DOWN);
                state = chunkAccess.getBlockState(blockPos);
            } while (blockPos.getY() > getWorld().getLevel().getMinBuildHeight() && !state.isAir());
        }
        do {
            blockPos.move(Direction.DOWN);
            state = chunkAccess.getBlockState(blockPos);
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
    }

    public Holder<Biome> scanBiome(BlockPos pos) {
        return getChunkHelper().getBiomeWithCaching(getWorld(), pos);
    }

    public static boolean isGlass(BlockState state) {
        return state.is(Blocks.GLASS) || state.is(Blocks.GLASS_PANE) ||
                state.getBlock() instanceof StainedGlassBlock ||
                state.getBlock() instanceof StainedGlassPaneBlock;
    }
}
