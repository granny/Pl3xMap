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
import net.pl3x.map.render.heightmap.Heightmap;
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

        this.mapWorld.getConfig().RENDER_SCANNERS.forEach(name -> {
            Renderer renderer = Renderers.INSTANCE.createRenderer(name, this.render, this.region);
            if (renderer != null) {
                this.renderers.add(renderer);
            }
        });
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
        this.renderers.forEach(Renderer::allocateImages);

        // scan chunks in region
        for (int chunkX = this.region.getChunkX(); chunkX < this.region.getChunkX() + 32; chunkX++) {
            for (int chunkZ = this.region.getChunkZ(); chunkZ < this.region.getChunkZ() + 32; chunkZ++) {
                // make sure render task is still running
                if (this.render.isCancelled()) {
                    // don't forget to increment chunk counter
                    this.render.getProgress().getProcessedChunks().getAndIncrement();
                    this.chunkHelper.clear();
                    return;
                }

                // make sure we're allowed to scan this chunk
                if (!this.area.containsChunk(chunkX, chunkZ)) {
                    // don't forget to increment chunk counter
                    this.render.getProgress().getProcessedChunks().getAndIncrement();
                    continue;
                }

                // pause here if we have to
                while (this.mapWorld.isPaused()) {
                    this.render.sleep(500);
                }

                // scan the chunk
                scanChunk(chunkX, chunkZ);

                // we're done with this chunk \o/
                this.render.getProgress().getProcessedChunks().incrementAndGet();
            }
        }

        // save images to disk
        if (!this.render.isCancelled()) {
            // submit to IO executor, so we can move on to next region without waiting
            this.render.getImageExecutor().submit(() -> {
                // surround in try/catch because executor eats exceptions
                try {
                    this.renderers.forEach(Renderer::saveImages);
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        // we're done with this region \o/
        this.render.getProgress().getProcessedRegions().getAndIncrement();
        this.chunkHelper.clear();
    }

    public void scanChunk(int chunkX, int chunkZ) {
        ChunkAccess chunk = this.chunkHelper.getChunk(this.mapWorld.getLevel(), chunkX, chunkZ);
        if (chunk == null) {
            return;
        }

        // world coordinates for most northwest block in chunk
        int blockX = Coordinate.chunkToBlock(chunkX);
        int blockZ = Coordinate.chunkToBlock(chunkZ);

        BlockPos.MutableBlockPos blockPos = new BlockPos.MutableBlockPos();
        Heightmap heightmap = this.mapWorld.getConfig().RENDER_HEIGHTMAP_TYPE.createHeightmap();

        // iterate each block in this chunk
        for (int z = 0; z < 16; z++) {

            // we need the bottom row of the chunk to the north to get heightmap correct
            if (z == 0 && this.mapWorld.getConfig().RENDER_HEIGHTMAP_TYPE != Heightmap.Type.DYNMAP) {
                scanNorthChunk(chunkX, chunkZ, blockX, blockZ, blockPos, heightmap);
            }

            for (int x = 0; x < 16; x++) {

                // we need the right row of the chunk to the west to get heightmap correct
                if (x == 0 && z == 0 && this.mapWorld.getConfig().RENDER_HEIGHTMAP_TYPE == Heightmap.Type.MODERN) {
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
        if (this.mapWorld.getLevel().dimensionType().hasCeiling()) {
            do {
                blockPos.move(Direction.DOWN);
                blockState = chunk.getBlockState(blockPos);
            } while (blockPos.getY() > this.mapWorld.getLevel().getMinBuildHeight() && !blockState.isAir());
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
            if (this.mapWorld.getConfig().RENDER_TRANSLUCENT_GLASS && isGlass(blockState)) {
                glass.add(blockColor);
                continue;
            }
            if (blockColor > 0) {
                break;
            }
        } while (blockPos.getY() > this.mapWorld.getLevel().getMinBuildHeight());

        BlockState fluidState = null;
        Biome fluidBiome = null;
        if (fluidPos != null) {
            fluidState = chunk.getBlockState(fluidPos);
            fluidBiome = this.chunkHelper.getBiomeWithCaching(this.mapWorld, fluidPos).value();
        }

        for (Renderer renderer : this.renderers) {
            renderer.doIt(this.mapWorld, chunk, blockState, blockPos, blockBiome, fluidState, fluidPos, fluidBiome, x, z, glass, heightmap, blockColor);
        }

        heightmap.update(fluidPos == null ? blockPos : fluidPos, x, z);
    }

    public void scanNorthChunk(int chunkX, int chunkZ, int blockX, int blockZ, BlockPos.MutableBlockPos blockPos, Heightmap heightmap) {
        ChunkAccess northChunk = this.chunkHelper.getChunk(this.mapWorld.getLevel(), chunkX, chunkZ - 1);
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
        ChunkAccess westChunk = this.chunkHelper.getChunk(this.mapWorld.getLevel(), chunkX - 1, chunkZ);
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
        if (this.mapWorld.getLevel().dimensionType().hasCeiling()) {
            do {
                blockPos.move(Direction.DOWN);
                state = chunkAccess.getBlockState(blockPos);
            } while (blockPos.getY() > this.mapWorld.getLevel().getMinBuildHeight() && !state.isAir());
        }
        do {
            blockPos.move(Direction.DOWN);
            state = chunkAccess.getBlockState(blockPos);
            if (!state.getFluidState().isEmpty()) {
                continue;
            }
            if (this.mapWorld.getConfig().RENDER_TRANSLUCENT_GLASS && isGlass(state)) {
                continue;
            }
            if (Colors.getRawBlockColor(state) > 0) {
                break;
            }
        } while (blockPos.getY() > this.mapWorld.getLevel().getMinBuildHeight());
    }

    public Holder<Biome> scanBiome(BlockPos pos) {
        return this.chunkHelper.getBiomeWithCaching(this.mapWorld, pos);
    }

    public static boolean isGlass(BlockState state) {
        return state.is(Blocks.GLASS) || state.is(Blocks.GLASS_PANE) ||
                state.getBlock() instanceof StainedGlassBlock ||
                state.getBlock() instanceof StainedGlassPaneBlock;
    }
}
