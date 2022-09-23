package net.pl3x.map.render.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.Executors;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.render.Area;
import net.pl3x.map.render.ScanTask;
import net.pl3x.map.world.World;

public class BackgroundRender extends Render {
    public BackgroundRender(World world) {
        super(world, Pl3xMap.api().getConsole(), 0, 0,
                Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Background").build()),
                Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("Pl3xMap-IO").build()));
    }

    @Override
    public void run() {
        try {
            while (Pl3xMap.api().getCurrentTick() < 20) {
                // server is not running yet
                sleep(1000);
            }
            render();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void render() {
        // don't scan any regions outside the world border
        Area scannableArea = new Area(getWorld().getLevel().getWorldBorder());

        // send regions to executor to scan
        int count = 0;
        int max = getWorld().getConfig().RENDER_BACKGROUND_MAX_REGIONS_PER_INTERVAL;
        while (getWorld().hasModifiedRegions() && count < max) {
            RegionCoordinate region = getWorld().getNextModifiedRegion();
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
