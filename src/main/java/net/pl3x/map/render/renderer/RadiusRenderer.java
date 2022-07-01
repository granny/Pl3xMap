package net.pl3x.map.render.renderer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.minecraft.world.level.ChunkPos;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.render.renderer.iterator.ChunkSpiralIterator;
import net.pl3x.map.render.renderer.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.render.renderer.iterator.coordinate.Coordinate;
import net.pl3x.map.render.renderer.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.renderer.progress.Progress;
import net.pl3x.map.render.scanner.Scanner;
import net.pl3x.map.render.scanner.Scanners;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;

public class RadiusRenderer extends Renderer {
    private final int radius;
    private long timeStarted;

    public RadiusRenderer(MapWorld mapWorld, Audience starter, int radius, int centerX, int centerZ) {
        super(mapWorld, starter, centerX, centerZ);
        this.radius = Coordinate.blockToChunk(radius);
    }

    @Override
    public void render() {
        this.timeStarted = System.currentTimeMillis();

        Lang.send(getStarter(), Lang.COMMAND_RADIUSRENDER_OBTAINING_CHUNKS);

        List<RegionCoordinate> regionFiles = new ArrayList<>();
        List<Path> files = FileUtil.getRegionFiles(getWorld().getLevel());
        for (Path path : files) {
            if (isCancelled()) {
                return;
            }

            if (path.toFile().length() == 0) {
                continue;
            }

            String filename = path.getFileName().toString();
            String[] split = filename.split("\\.");
            int x, z;
            try {
                x = Integer.parseInt(split[1]);
                z = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                continue;
            }

            regionFiles.add(new RegionCoordinate(x, z));
        }

        ChunkSpiralIterator spiral = new ChunkSpiralIterator(
                Coordinate.blockToChunk(getCenterX()),
                Coordinate.blockToChunk(getCenterZ()),
                this.radius);

        Map<RegionCoordinate, List<Long>> regions = new LinkedHashMap<>();

        long totalChunks = 0;

        while (spiral.hasNext()) {
            if (isCancelled()) {
                return;
            }

            ChunkCoordinate chunk = spiral.next();
            RegionCoordinate region = new RegionCoordinate(chunk.getRegionX(), chunk.getRegionZ());

            if (!regionFiles.contains(region)) {
                continue;
            }

            List<Long> list = regions.computeIfAbsent(region, k -> new ArrayList<>());
            list.add(ChunkPos.asLong(chunk.getChunkX(), chunk.getChunkZ()));

            totalChunks++;
        }

        List<Scanner> scannerTasks = new ArrayList<>();

        regions.forEach((region, list) -> scannerTasks.add(Scanners.INSTANCE.createScanner("basic", this, region, list)));

        getProgress().setTotalRegions(regions.size());
        getProgress().setTotalChunks(totalChunks);

        Lang.send(getStarter(), Lang.COMMAND_RADIUSRENDER_FOUND_TOTAL_CHUNKS
                .replace("<total>", Long.toString(getProgress().getTotalChunks())));

        Lang.send(getStarter(), Lang.COMMAND_RADIUSRENDER_USE_STATUS_FOR_PROGRESS);

        scannerTasks.forEach(getRenderExecutor()::submit);
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
    public void onCancel(boolean unloading) {
        Component component = Lang.parse(Lang.COMMAND_RADIUSRENDER_CANCELLED,
                Placeholder.unparsed("world", getWorld().getName()));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Bukkit.getConsoleSender())) {
            Lang.send(Bukkit.getConsoleSender(), component);
        }
    }
}
