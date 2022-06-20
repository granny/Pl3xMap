package net.pl3x.map.render.task;

import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.iterator.RegionSpiralIterator;
import net.pl3x.map.render.iterator.coordinate.Coordinate;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.render.queue.ScanRegion;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FullRender extends AbstractRender {
    private int maxRadius = 0;

    public FullRender(MapWorld mapWorld) {
        super(mapWorld, "FullRender", true, true, true, true);
    }

    @Override
    public void render() {
        Logger.info(Lang.COMMAND_FULLRENDER_STARTING
                .replace("<world>", getWorld().getName()));

        Logger.info(Lang.COMMAND_FULLRENDER_OBTAINING_REGIONS);

        List<RegionCoordinate> regionFiles = new ArrayList<>();
        for (Path path : FileUtil.getRegionFiles(getWorld().getLevel())) {
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
                Logger.warn(Lang.COMMAND_FULLRENDER_ERROR_PARSING_REGION_FILE
                        .replace("<path>", path.toString())
                        .replace("<filename>", filename));
                e.printStackTrace();
                continue;
            }

            RegionCoordinate region = new RegionCoordinate(x, z);
            regionFiles.add(region);

            this.maxRadius = Math.max(Math.max(this.maxRadius, Math.abs(x)), Math.abs(z));
        }

        RegionSpiralIterator spiral = new RegionSpiralIterator(
                Coordinate.blockToRegion(getCenterX()),
                Coordinate.blockToRegion(getCenterZ()),
                this.maxRadius);

        Logger.info(Lang.COMMAND_FULLRENDER_SORTING_REGIONS);

        Map<RegionCoordinate, ScanRegion> tasks = new LinkedHashMap<>();

        int failsafe = 0;
        while (spiral.hasNext()) {
            if (isCancelled()) {
                return;
            }

            if (failsafe > 500000) {
                // we scanned over half a million non-existent regions straight
                // quit the spiral and add the remaining regions to the end
                regionFiles.forEach(region -> tasks.put(region, new ScanRegion(this, region)));
                break;
            }

            RegionCoordinate region = spiral.next();
            if (regionFiles.remove(region)) {
                tasks.put(region, new ScanRegion(this, region));
                failsafe = 0;
            } else {
                failsafe++;
            }
        }

        getProgress().setTotalRegions(tasks.size());

        Logger.info(Lang.COMMAND_FULLRENDER_FOUND_TOTAL_REGIONS
                .replace("<total>", Integer.toString(getProgress().getTotalRegions())));

        Logger.info(Lang.COMMAND_FULLRENDER_USE_STATUS_FOR_PROGRESS);

        tasks.forEach((region, task) -> ThreadManager.INSTANCE.getRenderExecutor().submit(task));
    }
}
