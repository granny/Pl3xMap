package net.pl3x.map.render.job;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.job.iterator.RegionSpiralIterator;
import net.pl3x.map.render.job.iterator.coordinate.Coordinate;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.job.progress.Progress;
import net.pl3x.map.render.task.ScanTask;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;

public class FullRender extends Render {
    private long timeStarted;

    public FullRender(MapWorld mapWorld, Audience starter) {
        super(mapWorld, starter);
    }

    @Override
    public void render() {
        this.timeStarted = System.currentTimeMillis();

        // order preserved map of regions with boolean to signify if it was already scanned
        LinkedHashMap<RegionCoordinate, Boolean> regionsToScan = new LinkedHashMap<>();

        // don't scan any chunks outside the world border
        Area scannableArea = new Area(getWorld().getLevel().getWorldBorder());

        // check if we have any data to resume
        LinkedHashMap<RegionCoordinate, Boolean> resumedMap = getWorld().getScannedRegions();
        if (!resumedMap.isEmpty()) {
            Lang.send(getStarter(), Lang.COMMAND_FULLRENDER_RESUMED_RENDERING,
                    Placeholder.unparsed("world", getWorld().getName()));

            // add regions from previous run
            regionsToScan.putAll(resumedMap);

            // start the progress output
            getProgress().showChat(getStarter());
        } else {
            Lang.send(getStarter(), Lang.COMMAND_FULLRENDER_OBTAINING_REGIONS);

            // max radius for spiral iterator will be determined by the farthest region file found
            int maxRadius = 0;

            // scan region folder for existing region files
            List<RegionCoordinate> regionFiles = new ArrayList<>();
            List<Path> files = FileUtil.getRegionFiles(getWorld().getLevel());
            for (Path path : files) {
                if (isCancelled()) {
                    return;
                }

                // ignore empty region files
                if (path.toFile().length() == 0) {
                    Logger.debug(String.format("Ignoring region file (empty region) %s", path.getFileName()));
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
                    Logger.warn(Lang.COMMAND_FULLRENDER_ERROR_PARSING_REGION_FILE
                            .replace("<path>", path.toString())
                            .replace("<filename>", filename));
                    e.printStackTrace();
                    continue;
                }

                // check if region is inside scannable area
                if (!scannableArea.containsRegion(x, z)) {
                    Logger.debug(String.format("Ignoring region file (outside scannable area) %s", path.getFileName()));
                    continue;
                }

                // add known region
                regionFiles.add(new RegionCoordinate(x, z));

                // update max radius for spiral iterator
                maxRadius = Math.max(Math.max(maxRadius, Math.abs(x)), Math.abs(z));
            }

            // create spiral iterator to order region scanning
            RegionSpiralIterator spiral = new RegionSpiralIterator(
                    Coordinate.blockToRegion(getCenterX()),
                    Coordinate.blockToRegion(getCenterZ()),
                    maxRadius);

            // iterate the spiral
            int failsafe = 0;
            while (spiral.hasNext()) {
                if (isCancelled()) {
                    return;
                }

                // let us not get stuck in an endless loop
                if (failsafe > 500000) {
                    Logger.debug("Failsafe triggered.");
                    // we scanned over half a million non-existent regions straight
                    // quit the spiral and add the remaining regions to the end
                    regionFiles.forEach(region -> regionsToScan.put(region, false));
                    break;
                }

                // get region from spiral and ensure a region file exists for it
                RegionCoordinate region = spiral.next();
                if (regionFiles.remove(region)) {
                    // file exists, add region to scan
                    regionsToScan.put(region, false);
                    failsafe = 0;
                } else {
                    failsafe++;
                }
            }

            // store all regions we're scanning in case we have to resume after restart
            getWorld().setScannedRegions(regionsToScan);
        }

        if (isCancelled()) {
            return;
        }

        // add regions to executor tasks that will do the heavy lifting
        List<ScanTask> rendererTasks = new ArrayList<>();
        regionsToScan.forEach((region, done) -> {
            // only create a task for regions not already scanned
            if (!done) {
                rendererTasks.add(new ScanTask(this, region, scannableArea));
            }
        });

        // set our total progress values
        getProgress().setTotalRegions(rendererTasks.size());
        getProgress().setTotalChunks(getProgress().getTotalRegions() * 32L * 32L);

        // set our processed (done) progress values
        int done = (int) regionsToScan.values().stream().filter(bool -> bool).count();
        if (done > 0) {
            getProgress().setProcessedRegions(done);
            getProgress().setProcessedChunks(done * 32L * 32L);
        }

        // notify what we found
        if (done < 1) {
            Lang.send(getStarter(), Lang.COMMAND_FULLRENDER_FOUND_TOTAL_REGIONS,
                    Placeholder.unparsed("total", Long.toString(getProgress().getTotalRegions())));
        } else {
            float percent = ((float) getProgress().getProcessedChunks().get() / (float) getProgress().getTotalChunks()) * 100.0F;
            Lang.send(getStarter(), Lang.COMMAND_FULLRENDER_RESUMED_TOTAL_REGIONS,
                    Placeholder.unparsed("total", Long.toString(getProgress().getTotalRegions())),
                    Placeholder.unparsed("done", Long.toString(done)),
                    Placeholder.unparsed("percent", String.format("%.2f", percent))
            );
        }
        Lang.send(getStarter(), Lang.COMMAND_FULLRENDER_USE_STATUS_FOR_PROGRESS);

        // send the tasks to executor to run
        rendererTasks.forEach(getRenderExecutor()::submit);
    }

    @Override
    public void onStart() {
        Component component = Lang.parse(Lang.COMMAND_FULLRENDER_STARTING,
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
        Component component = Lang.parse(Lang.COMMAND_FULLRENDER_FINISHED,
                Placeholder.unparsed("world", getWorld().getName()),
                Placeholder.parsed("elapsed", elapsed));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Bukkit.getConsoleSender())) {
            Lang.send(Bukkit.getConsoleSender(), component);
        }
        getWorld().clearScannedRegions();
    }

    @Override
    public void onCancel(boolean unloading) {
        if (unloading) {
            // don't do anything if we're unloading
            return;
        }
        Component component = Lang.parse(Lang.COMMAND_FULLRENDER_CANCELLED,
                Placeholder.unparsed("world", getWorld().getName()));
        Lang.send(getStarter(), component);
        if (!getStarter().equals(Bukkit.getConsoleSender())) {
            Lang.send(Bukkit.getConsoleSender(), component);
        }
        getWorld().clearScannedRegions();
    }
}
