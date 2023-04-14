package net.pl3x.map.core.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.util.SpiralIterator;
import net.pl3x.map.core.world.World;

public class RenderWorld implements Runnable {
    private final World world;

    private long timeStarted;

    public RenderWorld(World world) {
        this.world = world;
    }

    @Override
    public void run() {
        try {
            System.out.println("START");

            this.timeStarted = System.currentTimeMillis();

            // scan region folder for existing regions
            Collection<Point> regionPositions = this.world.listRegions();

            // find max radius for spiral iterator
            int maxRadius = 0;
            for (Point pos : regionPositions) {
                maxRadius = Math.max(Math.max(maxRadius, Math.abs(pos.x())), Math.abs(pos.z()));
            }

            // create spiral iterator to order region scanning
            Point spawn = this.world.getSpawn();
            SpiralIterator spiralIterator = new SpiralIterator(spawn.x(), spawn.z(), maxRadius);

            // order preserved map of regions with boolean to signify if it was already scanned
            List<Point> orderedRegionsToScan = new ArrayList<>();

            // iterate the spiral
            int numberOfSkippedRegions = 0;
            while (spiralIterator.hasNext()) {
                // let us not get stuck in an endless loop
                if (numberOfSkippedRegions > 500000) {
                    Logger.debug("Failsafe triggered.");
                    // we scanned over half a million non-existent regions straight
                    // quit the spiral and add the remaining regions to the end
                    orderedRegionsToScan.addAll(regionPositions);
                    break;
                }

                // get region from spiral and ensure a region file exists for it
                Point regionPos = spiralIterator.next();
                if (regionPositions.remove(regionPos)) {
                    // file exists, add region to scan
                    orderedRegionsToScan.add(regionPos);
                    numberOfSkippedRegions = 0;
                } else {
                    numberOfSkippedRegions++;
                }
            }

            // create and send tasks to executor to run
            CompletableFuture.allOf(orderedRegionsToScan.stream()
                    .map(pos -> CompletableFuture.runAsync(new ScanRegion(this.world, pos), Pl3xMap.api().getExecutor())
                            .whenComplete((result, throwable) -> {
                                if (throwable != null) {
                                    throwable.printStackTrace();
                                }

                                // free up some memory - todo is this cleanup needed?
                                //task.cleanup();

                                // run the garbage collector
                                if (Config.GC_WHEN_RUNNING) {
                                    System.gc();
                                }
                            })
                    ).toArray(CompletableFuture[]::new)
            ).whenComplete((result, throwable) -> {
                if (throwable != null) {
                    throwable.printStackTrace();
                }

                // free up some memory
                this.world.cleanup();

                // todo temp output
                long diff = System.currentTimeMillis() - this.timeStarted;
                System.out.println("FINISHED in " + (diff / 1000) + " seconds");

                // run the garbage collector
                if (Config.GC_WHEN_FINISHED) {
                    System.gc();
                }
            });
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
