/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.renderer.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executor;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.util.SpiralIterator;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RegionProcessor {
    private final Deque<@NonNull Ticket> regionsToScan = new ConcurrentLinkedDeque<>();
    private final Executor executor;

    private CompletableFuture<@NonNull Void> future;

    private long timeStarted;
    private boolean running;

    public RegionProcessor() {
        this.executor = Pl3xMap.ThreadFactory.createService("Pl3xMap-Processor");
    }

    public void start(long delay) {
        this.future = CompletableFuture.runAsync(() -> {
            // wait 10 seconds...
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignore) {
            }

            // run the task
            run();

            // rinse and repeat
            start(10000L);
        }, this.executor);
    }

    public void stop() {
        if (this.future != null) {
            boolean result = this.future.cancel(true);
            Logger.debug("Stopped region processor: " + result);
        }
    }

    public void addRegions(@NonNull World world, @NonNull Collection<@NonNull Point> regions) {
        for (Point region : regions) {
            this.regionsToScan.add(new Ticket(world, region));
        }
    }

    private void run() {
        if (this.running) {
            // this task is already running
            Logger.debug("Region processor already running!");
            return;
        }

        // consider task as running
        this.running = true;
        this.timeStarted = System.currentTimeMillis();

        Logger.debug("Region processor started queuing at " + this.timeStarted);

        try {
            Map<World, Collection<Point>> map = new HashMap<>();
            while (!this.regionsToScan.isEmpty()) {
                Ticket ticket = this.regionsToScan.poll();
                Collection<Point> set = map.getOrDefault(ticket.world, new HashSet<>());
                set.add(ticket.region);
                map.put(ticket.world, set);
            }

            for (Map.Entry<World, Collection<Point>> entry : map.entrySet()) {
                process(entry.getKey(), entry.getValue());
            }

            if (map.isEmpty()) {
                this.running = false;
            }
        } catch (Throwable t) {
            t.printStackTrace();
            this.running = false;
        }

        Logger.debug("Region processor finished queuing at " + System.currentTimeMillis());
    }

    private void process(@NonNull World world, @NonNull Collection<@NonNull Point> regionPositions) {
        Logger.debug(world.getName() + " Region processor started processing at " + System.currentTimeMillis());

        // find max radius for spiral iterator
        int maxRadius = 0;
        for (Point pos : regionPositions) {
            maxRadius = Math.max(Math.max(maxRadius, Math.abs(pos.x())), Math.abs(pos.z()));
        }

        // create spiral iterator to order region scanning
        Point spawn = world.getSpawn();
        SpiralIterator spiralIterator = new SpiralIterator(spawn.x() >> 9, spawn.z() >> 9, maxRadius);

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
        schedule(world, orderedRegionsToScan);

        Logger.debug(world.getName() + " Region processor finished processing at " + System.currentTimeMillis());
    }

    private void schedule(@NonNull World world, @NonNull List<@NonNull Point> orderedRegionsToScan) {
        CompletableFuture.allOf(orderedRegionsToScan.stream()
                .map(pos -> CompletableFuture.runAsync(new RegionScanTask(world, pos), Pl3xMap.api().getRenderExecutor())
                        .whenComplete((result, throwable) -> {
                            if (throwable != null) {
                                throwable.printStackTrace();
                            }

                            // set region modified time
                            world.getRegionModifiedState().set(Mathf.asLong(pos), this.timeStarted);

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

            // save region modified times
            world.getRegionModifiedState().save();

            // free up some memory
            world.cleanup();

            // run the garbage collector
            if (Config.GC_WHEN_FINISHED) {
                System.gc();
            }

            // consider task as no longer running
            this.running = false;

            Logger.debug(world.getName() + " Region processor finished task at " + System.currentTimeMillis());
        });
    }

    private record Ticket(@NonNull World world, @NonNull Point region) {
    }
}
