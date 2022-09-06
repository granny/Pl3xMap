package net.pl3x.map.render.job;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.coordinate.ChunkCoordinate;
import net.pl3x.map.coordinate.Coordinate;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.iterator.ChunkSpiralIterator;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.ScanTask;
import net.pl3x.map.render.job.progress.Progress;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;

public class RadiusRender extends Render {
    private final int radius;
    private long timeStarted;

    public RadiusRender(MapWorld mapWorld, Audience starter, int radius, int centerX, int centerZ) {
        super(mapWorld, starter, centerX, centerZ);
        this.radius = Coordinate.blockToChunk(radius);
    }

    @Override
    public void render() {
        this.timeStarted = System.currentTimeMillis();

        // notify we're getting things set up
        Lang.send(getStarter(), Lang.COMMAND_RADIUSRENDER_OBTAINING_CHUNKS);

        // find center chunk
        int chunkCenterX = Coordinate.blockToChunk(getCenterX());
        int chunkCenterZ = Coordinate.blockToChunk(getCenterZ());

        // set our scannable area from radius around center
        Area scannableArea = new Area(chunkCenterX - radius, chunkCenterZ - radius, chunkCenterX + radius, chunkCenterZ + radius);

        // get a list of all existing regions
        List<RegionCoordinate> regionFiles = new ArrayList<>();
        List<Path> files = FileUtil.getRegionFiles(getMapWorld().getWorld().getLevel());
        for (Path path : files) {
            // exit if cancelled
            if (isCancelled()) {
                return;
            }

            // ignore empty region files
            if (path.toFile().length() == 0) {
                continue;
            }

            // get region coords from filename
            String filename = path.getFileName().toString();
            String[] split = filename.split("\\.");
            int x, z;
            try {
                x = Integer.parseInt(split[1]);
                z = Integer.parseInt(split[2]);
            } catch (NumberFormatException e) {
                Logger.warn(Lang.COMMAND_RADIUSRENDER_ERROR_PARSING_REGION_FILE
                        .replace("<path>", path.toString())
                        .replace("<filename>", filename));
                e.printStackTrace();
                continue;
            }

            if (!scannableArea.containsRegion(x, z)) {
                continue;
            }

            // add known region
            regionFiles.add(new RegionCoordinate(x, z));
        }

        // create spiral iterator to order chunk scanning
        ChunkSpiralIterator chunkSpiral = new ChunkSpiralIterator(chunkCenterX, chunkCenterZ, this.radius);

        // use a LinkedHashSet in order to keep regions in spiral order
        LinkedHashSet<RegionCoordinate> regions = new LinkedHashSet<>();

        // iterate chunks to add them to regions list
        long totalChunks = 0;
        while (chunkSpiral.hasNext()) {
            // exit if cancelled
            if (isCancelled()) {
                return;
            }

            // get region for chunk
            ChunkCoordinate chunk = chunkSpiral.next();
            RegionCoordinate region = new RegionCoordinate(chunk.getRegionX(), chunk.getRegionZ());

            // this region does not exist, ignore
            if (!regionFiles.contains(region)) {
                continue;
            }

            // we'll use this region
            regions.add(region);

            // increment chunks count for progress
            totalChunks++;
        }

        // create list of render tasks
        List<ScanTask> rendererTasks = new ArrayList<>();
        regions.forEach(region -> rendererTasks.add(new ScanTask(this, region, scannableArea)));

        // set progress totals
        getProgress().setTotalRegions(regions.size());
        getProgress().setTotalChunks(totalChunks);

        // notify what we found
        Lang.send(getStarter(), Lang.COMMAND_RADIUSRENDER_FOUND_TOTAL_CHUNKS
                .replace("<total>", Long.toString(getProgress().getTotalChunks())));
        Lang.send(getStarter(), Lang.COMMAND_RADIUSRENDER_USE_STATUS_FOR_PROGRESS);

        // send the tasks to executor to run
        rendererTasks.forEach(getRenderExecutor()::submit);
    }

    @Override
    public void onStart() {
        Component component = Lang.parse(Lang.COMMAND_RADIUSRENDER_STARTING,
                Placeholder.unparsed("world", getMapWorld().getWorld().getName()));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Pl3xMap.api().getConsole())) {
            Lang.send(Pl3xMap.api().getConsole(), component);
        }
    }

    @Override
    public void onFinish() {
        long timeEnded = System.currentTimeMillis();
        String elapsed = Progress.formatMilliseconds(timeEnded - this.timeStarted);
        Component component = Lang.parse(Lang.COMMAND_RADIUSRENDER_FINISHED,
                Placeholder.unparsed("world", getMapWorld().getWorld().getName()),
                Placeholder.parsed("elapsed", elapsed));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Pl3xMap.api().getConsole())) {
            Lang.send(Pl3xMap.api().getConsole(), component);
        }
    }

    @Override
    public void onCancel(boolean unloading) {
        Component component = Lang.parse(Lang.COMMAND_RADIUSRENDER_CANCELLED,
                Placeholder.unparsed("world", getMapWorld().getWorld().getName()));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Pl3xMap.api().getConsole())) {
            Lang.send(Pl3xMap.api().getConsole(), component);
        }
    }
}
