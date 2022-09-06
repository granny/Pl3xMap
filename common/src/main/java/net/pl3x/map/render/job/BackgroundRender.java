package net.pl3x.map.render.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.ScanTask;
import net.pl3x.map.world.MapWorld;

public class BackgroundRender extends Render {
    private static final ExecutorService BACKGROUND_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Background").build());
    private static final ExecutorService BACKGROUND_IO_EXECUTOR = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pl3xMap-IO").build());

    public BackgroundRender(MapWorld mapWorld) {
        super(mapWorld, Pl3xMap.api().getConsole(), 0, 0, BACKGROUND_EXECUTOR, BACKGROUND_IO_EXECUTOR);
    }

    @Override
    public void run() {
        while (Pl3xMap.api().getCurrentTick() < 20) {
            // server is not running yet
            sleep(1000);
        }
        render();
    }

    @Override
    public void render() {
        // don't scan any regions outside the world border
        Area scannableArea = new Area(getMapWorld().getWorld().getLevel().getWorldBorder());

        // send regions to executor to scan
        int count = 0;
        while (getMapWorld().hasModifiedRegions() && count < getMapWorld().getConfig().RENDER_BACKGROUND_MAX_REGIONS_PER_INTERVAL) {
            RegionCoordinate region = getMapWorld().getNextModifiedRegion();
            if (scannableArea.containsRegion(region.getRegionX(), region.getRegionZ())) {
                ScanTask scanTask = new ScanTask(this, region, scannableArea);
                getRenderExecutor().submit(scanTask);
                count++;
            }
        }
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
