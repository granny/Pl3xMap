package net.pl3x.map.render.task;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.world.level.ChunkPos;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.iterator.ChunkSpiralIterator;
import net.pl3x.map.render.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.render.iterator.coordinate.Coordinate;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.progress.Progress;
import net.pl3x.map.render.queue.ScanRegion;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RadiusRender extends AbstractRender {
    private final int centerX;
    private final int centerZ;
    private final int radius;
    private long timeStarted;

    public RadiusRender(MapWorld mapWorld, Audience starter, Location center, int radius) {
        super(mapWorld, "RadiusRender", starter);
        this.radius = Coordinate.blockToChunk(radius);
        this.centerX = Coordinate.blockToChunk(center.getBlockX());
        this.centerZ = Coordinate.blockToChunk(center.getBlockZ());
    }

    @Override
    public void render() {
        this.timeStarted = System.currentTimeMillis();

        Logger.debug(Lang.COMMAND_RADIUSRENDER_OBTAINING_CHUNKS);

        ChunkSpiralIterator spiral = new ChunkSpiralIterator(this.centerX, this.centerZ, this.radius);

        Map<RegionCoordinate, List<Long>> regions = new LinkedHashMap<>();

        long totalChunks = 0;

        while (spiral.hasNext()) {
            if (isCancelled()) {
                return;
            }

            ChunkCoordinate chunk = spiral.next();
            RegionCoordinate region = new RegionCoordinate(chunk.getRegionX(), chunk.getRegionZ());
            List<Long> list = regions.computeIfAbsent(region, k -> new ArrayList<>());
            list.add(ChunkPos.asLong(chunk.getChunkX(), chunk.getChunkZ()));

            totalChunks++;
        }

        List<ScanRegion> tasks = new ArrayList<>();

        regions.forEach((region, list) -> tasks.add(new ScanRegion(this, region, list)));

        getProgress().setTotalRegions(regions.size());
        getProgress().setTotalChunks(totalChunks);

        Logger.info(Lang.COMMAND_RADIUSRENDER_FOUND_TOTAL_CHUNKS
                .replace("<total>", Long.toString(getProgress().getTotalChunks())));

        Logger.info(Lang.COMMAND_RADIUSRENDER_USE_STATUS_FOR_PROGRESS);

        tasks.forEach(task -> ThreadManager.INSTANCE.getRenderExecutor().submit(task));
    }

    @Override
    public void onStart() {
        Component component = Lang.parse(Lang.COMMAND_RADIUSRENDER_STARTING,
                Placeholder.unparsed("world", getWorld().getName()));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Bukkit.getConsoleSender())) {
            Lang.send(Bukkit.getConsoleSender(), component);
        }
    }

    @Override
    public void onFinish() {
        long timeEnded = System.currentTimeMillis();
        String elapsed = Progress.formatMilliseconds(timeEnded - this.timeStarted);
        Component component = Lang.parse(Lang.COMMAND_RADIUSRENDER_FINISHED,
                Placeholder.unparsed("world", getWorld().getName()),
                Placeholder.parsed("elapsed", elapsed));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Bukkit.getConsoleSender())) {
            Lang.send(Bukkit.getConsoleSender(), component);
        }
    }

    @Override
    public void onCancel() {
        Component component = Lang.parse(Lang.COMMAND_RADIUSRENDER_CANCELLED,
                Placeholder.unparsed("world", getWorld().getName()));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Bukkit.getConsoleSender())) {
            Lang.send(Bukkit.getConsoleSender(), component);
        }
    }
}
