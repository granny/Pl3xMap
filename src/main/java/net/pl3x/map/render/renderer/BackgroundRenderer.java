package net.pl3x.map.render.renderer;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import net.minecraft.world.level.ChunkPos;
import net.pl3x.map.render.renderer.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.scanner.Scanner;
import net.pl3x.map.render.scanner.Scanners;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;

public class BackgroundRenderer extends Renderer {
    public BackgroundRenderer(MapWorld mapWorld) {
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
        while (getWorld().hasModifiedChunks() && chunks.size() < getWorld().getConfig().RENDER_BACKGROUND_MAX_CHUNKS_PER_INTERVAL) {
            chunks.add(getWorld().getNextModifiedChunk());
        }

        Map<RegionCoordinate, List<Long>> regions = new LinkedHashMap<>();

        chunks.forEach(chunk -> {
            RegionCoordinate region = new RegionCoordinate(chunk.getRegionX(), chunk.getRegionZ());
            List<Long> list = regions.computeIfAbsent(region, k -> new ArrayList<>());
            list.add(ChunkPos.asLong(chunk.getChunkX(), chunk.getChunkZ()));
        });

        List<Scanner> scannerTasks = new ArrayList<>();

        regions.forEach((region, list) -> {
            scannerTasks.add(Scanners.INSTANCE.createScanner("basic", this, region, list));
        });

        scannerTasks.forEach(getRenderExecutor()::submit);
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
