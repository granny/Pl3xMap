package net.pl3x.map.render.scanner;

import java.util.Collection;
import net.minecraft.world.level.ChunkPos;
import net.pl3x.map.render.image.Image;
import net.pl3x.map.render.renderer.Renderer;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.world.ChunkHelper;
import net.pl3x.map.world.MapWorld;

public abstract class Scanner implements Runnable {
    private final Renderer render;
    private final RegionCoordinate region;
    private final Collection<Long> chunks;

    private final MapWorld mapWorld;
    private final ChunkHelper chunkHelper;

    private Image.Holder imageHolder;

    public Scanner(Renderer render, RegionCoordinate region, Collection<Long> chunks) {
        this.render = render;
        this.region = region;
        this.chunks = chunks;

        this.mapWorld = render.getWorld();
        this.chunkHelper = new ChunkHelper(render);
    }

    public Renderer getRender() {
        return this.render;
    }

    public RegionCoordinate getRegion() {
        return this.region;
    }

    public Collection<Long> getChunks() {
        return this.chunks;
    }

    public MapWorld getWorld() {
        return this.mapWorld;
    }

    public ChunkHelper getChunkHelper() {
        return this.chunkHelper;
    }

    public Image.Holder getImageHolder() {
        return this.imageHolder;
    }

    public void setImageHolder(Image.Holder imageHolder) {
        this.imageHolder = imageHolder;
    }

    @Override
    public void run() {
        // wrap in try/catch because ExecutorService's Future swallows all exceptions :3
        try {
            scanRegion();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void scanRegion() {
        // allocate images
        this.imageHolder = new Image.Holder(getWorld(), getRegion());

        // scan chunks in region
        for (int chunkX = getRegion().getChunkX(); chunkX < getRegion().getChunkX() + 32; chunkX++) {
            for (int chunkZ = getRegion().getChunkZ(); chunkZ < getRegion().getChunkZ() + 32; chunkZ++) {
                if (!getChunks().isEmpty() && !getChunks().contains(ChunkPos.asLong(chunkX, chunkZ))) {
                    continue;
                }

                // make sure render task is still running
                if (getRender().isCancelled()) {
                    cleanup();
                    return;
                }

                while (getWorld().isPaused()) {
                    getRender().sleep(500);
                }

                scanChunk(chunkX, chunkZ);

                getRender().getProgress().getProcessedChunks().incrementAndGet();
            }
        }

        // save images to disk
        if (!getRender().isCancelled()) {
            getRender().getImageExecutor().submit(() -> {
                try {
                    this.imageHolder.save();
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            });
        }

        // we're done with this region \o/
        cleanup();
    }

    public abstract void scanChunk(int chunkX, int chunkZ);

    public void cleanup() {
        getChunkHelper().clear();
        getRender().getProgress().getProcessedRegions().getAndIncrement();
    }
}
