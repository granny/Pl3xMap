package net.pl3x.map.render.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.coordinate.BlockCoordinate;
import net.pl3x.map.api.coordinate.ChunkCoordinate;
import net.pl3x.map.api.coordinate.Coordinate;
import net.pl3x.map.api.coordinate.RegionCoordinate;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.world.ChunkHelper;
import net.pl3x.map.world.MapWorld;

public class ScanTask implements Runnable {
    private final Render render;
    private final RegionCoordinate region;
    private final Area area;

    private final MapWorld mapWorld;
    private final ChunkHelper chunkHelper;

    private final LinkedHashMap<String, Renderer> renderers = new LinkedHashMap<>();

    private final ScanData.Data scanData = new ScanData.Data();
    private final Set<ChunkCoordinate> scannableChunks = new HashSet<>();

    public ScanTask(Render render, RegionCoordinate region, Area area) {
        this.render = render;
        this.region = region;
        this.area = area;

        this.mapWorld = render.getWorld();
        this.chunkHelper = new ChunkHelper(render);

        List<String> renderers = new ArrayList<>();
        renderers.addAll(this.mapWorld.getConfig().RENDERERS_VISIBLE);
        renderers.addAll(this.mapWorld.getConfig().RENDERERS_HIDDEN);
        renderers.forEach(name -> {
            Renderer renderer = Pl3xMap.api().getRendererManager().createRenderer(name, this);
            if (renderer != null) {
                this.renderers.put(name, renderer);
            }
        });
    }

    public Render getRender() {
        return this.render;
    }

    public RegionCoordinate getRegion() {
        return this.region;
    }

    public MapWorld getWorld() {
        return this.mapWorld;
    }

    public ChunkHelper getChunkHelper() {
        return this.chunkHelper;
    }

    public Renderer getRenderer(String name) {
        return this.renderers.get(name);
    }

    public boolean isChunkScannable(int chunkX, int chunkZ) {
        if (this.scannableChunks.isEmpty()) {
            return true;
        }
        return this.scannableChunks.contains(new ChunkCoordinate(chunkX, chunkZ));
    }

    @Override
    public void run() {
        // wrap in try/catch because executor swallows all exceptions :3
        try {
            scanRegion();
            this.render.getProgress().getProcessedRegions().getAndIncrement();
            this.chunkHelper.clear();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void scanRegion() {
        // allocate images
        this.renderers.forEach((name, renderer) -> renderer.allocateData());

        int x = this.region.getChunkX();
        int z = this.region.getChunkZ();

        // scan data in chunks in region
        for (int chunkX = x - 1; chunkX < x + 32 + 1; chunkX++) {
            for (int chunkZ = z - 1; chunkZ < z + 32 + 1; chunkZ++) {
                // make sure render task is still running
                if (this.render.isCancelled()) {
                    return;
                }

                // pause here if we have to
                while (this.mapWorld.isPaused()) {
                    this.render.sleep(500);
                }

                if (chunkX < x || chunkX >= x + 32 || chunkZ < z || chunkZ >= z + 32) {
                    scanChunk(chunkX, chunkZ, true);
                } else {
                    scanChunk(chunkX, chunkZ, false);
                    this.render.getProgress().getProcessedChunks().getAndIncrement();
                }
            }
        }

        // run the renderers on scanned data
        this.renderers.forEach((name, renderer) -> renderer.scanData(this.region, this.scanData));

        // save images to disk
        if (!this.render.isCancelled()) {
            // submit to IO executor, so we can move on to next region without waiting
            this.render.getImageExecutor().submit(() -> {
                // surround in try/catch because executor eats exceptions
                try {
                    this.renderers.forEach((name, renderer) -> renderer.saveData());
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }
    }

    public void scanChunk(int chunkX, int chunkZ, boolean edge) {
        // make sure chunk is within scannable area
        if (!this.area.containsChunk(chunkX, chunkZ)) {
            return;
        }

        // check if chunk is scannable
        if (!isChunkScannable(chunkX, chunkZ)) {
            return;
        }

        // scan the chunk
        ChunkAccess chunk = this.chunkHelper.getChunk(this.mapWorld.getLevel(), chunkX, chunkZ);
        if (chunk == null) {
            return;
        }

        // world coordinates for most northwest block in chunk
        int blockX = Coordinate.chunkToBlock(chunkX);
        int blockZ = Coordinate.chunkToBlock(chunkZ);

        // iterate each block in this chunk
        for (int z = 0; z < 16; z++) {
            for (int x = 0; x < 16; x++) {
                BlockCoordinate coordinate = new BlockCoordinate(blockX + x, blockZ + z);
                ScanData data = new ScanData(this, chunk, coordinate);
                if (edge) {
                    this.scanData.edge(coordinate, data);
                } else {
                    this.scanData.put(coordinate, data);
                }
            }
        }
    }

    public void addChunk(ChunkCoordinate chunk) {
        this.scannableChunks.add(chunk);
    }
}
