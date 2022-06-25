package net.pl3x.map.render.queue;

import net.minecraft.world.level.ChunkPos;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.Image;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.render.task.ThreadManager;

import java.util.Collection;
import java.util.Collections;

public class ScanRegion extends AbstractScan {
    private final RegionCoordinate region;
    private final Collection<Long> chunks;

    public ScanRegion(AbstractRender render, RegionCoordinate region) {
        this(render, region, Collections.emptySet());
    }

    public ScanRegion(AbstractRender render, RegionCoordinate region, Collection<Long> chunks) {
        super(render);
        this.region = region;
        this.chunks = chunks;
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
        Logger.debug("[" + Thread.currentThread().getName() + "] Rendering region " + this.region.getRegionX() + ", " + this.region.getRegionZ());

        config = this.mapWorld.getConfig();
        level = this.mapWorld.getLevel();
        biomeColors = this.mapWorld.getBiomeColors();
        minY = level.getMinBuildHeight();

        // allocate images
        imageSet = new Image.Set(this.mapWorld, this.region);

        // scan chunks in region
        for (chunkX = this.region.getChunkX(); chunkX < this.region.getChunkX() + 32; chunkX++) {
            for (chunkZ = this.region.getChunkZ(); chunkZ < this.region.getChunkZ() + 32; chunkZ++) {
                if (!chunks.isEmpty() && !chunks.contains(ChunkPos.asLong(chunkX, chunkZ))) {
                    continue;
                }
                scanChunk();
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
}
