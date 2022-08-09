package net.pl3x.map.render.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.pl3x.map.api.coordinate.ChunkCoordinate;
import net.pl3x.map.api.coordinate.RegionCoordinate;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;

public class BackgroundRender extends Render {
    private static final ExecutorService BACKGROUND_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Background").build());
    private static final ExecutorService BACKGROUND_IO_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pl3xMap-IO").build());

    public BackgroundRender(MapWorld mapWorld) {
        super(mapWorld, Bukkit.getConsoleSender(), 0, 0, BACKGROUND_EXECUTOR, BACKGROUND_IO_EXECUTOR);
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

        // get the tasks ready
        Map<RegionCoordinate, ScanTask> regionScanTasks = new HashMap<>();
        chunks.forEach(chunk -> {
            RegionCoordinate region = new RegionCoordinate(chunk.getRegionX(), chunk.getRegionZ());
            ScanTask scanTask = regionScanTasks.get(region);
            if (scanTask == null) {
                scanTask = new ScanTask(this, region, scannableArea);
                regionScanTasks.put(region, scanTask);
            }
            scanTask.addChunk(chunk);
        });

        // send regions to executor to scan
        regionScanTasks.forEach((region, scanTask) -> getRenderExecutor().submit(scanTask));
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
