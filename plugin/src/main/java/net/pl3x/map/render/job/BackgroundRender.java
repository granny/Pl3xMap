package net.pl3x.map.render.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.job.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;

public class BackgroundRender extends Render {
    public BackgroundRender(MapWorld mapWorld) {
        super(mapWorld, Bukkit.getConsoleSender(), 0, 0,
                Executors.newFixedThreadPool(getThreads(mapWorld.getConfig().RENDER_BACKGROUND_RENDER_THREADS),
                        new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Background-%d").build()),
                Executors.newFixedThreadPool(getThreads(mapWorld.getConfig().RENDER_BACKGROUND_RENDER_THREADS),
                        new ThreadFactoryBuilder().setNameFormat("Pl3xMap-IO-%d").build())
        );
    }

    @Override
    public void run() {
        while (Bukkit.getCurrentTick() < 20) {
            // server is not running yet
            sleep(1000);
        }
        render();
    }

    @Override
    public void render() {
        Set<ChunkCoordinate> chunks = new HashSet<>();

        // don't scan any chunks outside the world border
        Area scannableArea = new Area(getWorld().getLevel().getWorldBorder());

        // get modified chunks to render this interval
        while (getWorld().hasModifiedChunks() && chunks.size() < getWorld().getConfig().RENDER_BACKGROUND_MAX_CHUNKS_PER_INTERVAL) {
            ChunkCoordinate chunk = getWorld().getNextModifiedChunk();
            if (scannableArea.containsChunk(chunk.getChunkX(), chunk.getChunkZ())) {
                chunks.add(chunk);
            }
        }

        // create set of regions from all the modified chunks
        Set<RegionCoordinate> regions = new HashSet<>();
        chunks.forEach(chunk -> regions.add(new RegionCoordinate(chunk.getRegionX(), chunk.getRegionZ())));

        // send regions to executor to scan
        regions.forEach(region -> getRenderExecutor().submit(new ScanTask(this, region, scannableArea)));
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onCancel(boolean unloading) {
    }
}
