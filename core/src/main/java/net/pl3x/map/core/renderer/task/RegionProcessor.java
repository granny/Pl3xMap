/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.renderer.progress.Progress;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.util.SpiralIterator;
import net.pl3x.map.core.world.World;
import org.jspecify.annotations.NullMarked;

@NullMarked
public class RegionProcessor {
    // bounds how long schedule() will wait for a world's region scan tasks
    // before giving up, instead of blocking the sole processor thread
    // forever with zero error output on a stuck/orphaned task
    private static final long SCHEDULE_TIMEOUT_MINUTES = 60;

    private final Map<World, Collection<Point>> regionsToScan = new ConcurrentHashMap<>();
    private final Deque<Ticket> ticketsToScan = new ConcurrentLinkedDeque<>();

    // guards creation/replacement of the executor field so start() and
    // stop() can never race each other while swapping it out
    private final Object executorLock = new Object();

    // non-final -- stop() may shut this down permanently, and start() must
    // be able to lazily create a fresh one afterward so the processor can
    // actually resume/restart after a reload cycle
    private ExecutorService executor;

    private final Progress progress;

    private volatile CompletableFuture<Void> future;

    private volatile boolean paused;

    private long timeStarted;
    private volatile boolean running;

    public RegionProcessor() {
        this.executor = Pl3xMap.ThreadFactory.createService("Pl3xMap-Processor");
        this.progress = new Progress();
    }

    /**
     * Ensures {@link #executor} is a live, usable executor, transparently
     * replacing it with a fresh one if it was previously shut down (e.g.
     * by {@link #stop()}), so this instance can be stopped and later
     * resumed/restarted indefinitely.
     */
    private ExecutorService getOrCreateExecutor() {
        synchronized (this.executorLock) {
            if (this.executor.isShutdown() || this.executor.isTerminated()) {
                Logger.debug("Region processor executor was shut down; creating a fresh one.");
                this.executor = Pl3xMap.ThreadFactory.createService("Pl3xMap-Processor");
            }
            return this.executor;
        }
    }

    @SuppressWarnings("BusyWait")
    public void checkPaused() {
        while (isPaused()) {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ignore) {
            }
        }
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public Progress getProgress() {
        return this.progress;
    }

    public Set<World> getQueuedWorlds() {
        return this.regionsToScan.keySet();
    }

    public void start(long delay) {
        ExecutorService liveExecutor = getOrCreateExecutor();

        this.future = CompletableFuture.runAsync(() -> {
            // wait...
            try {
                Thread.sleep(delay);
            } catch (InterruptedException ignore) {
                // interrupted (e.g. via a concurrent stop()) -- don't keep
                // the self-rescheduling loop alive, let this chain die
                // cleanly instead of calling run() and re-arming again
                return;
            }

            // run the task
            if (!isPaused()) {
                run();
            }

            // rinse and repeat
            start(5000L);
        }, liveExecutor);
    }

    /**
     * Stops the processor, genuinely interrupting any in-progress work via
     * {@code shutdownNow()}. The processor remains fully reusable
     * afterward: the next call to {@link #start(long)} will transparently
     * create a fresh executor.
     */
    public void stop() {
        this.progress.stop();

        if (this.future != null) {
            boolean result = this.future.cancel(true);
            Logger.debug("Stopped region processor: " + result);
        }

        synchronized (this.executorLock) {
            this.executor.shutdownNow();
        }

        this.running = false;
    }

    public void addRegions(World world, Collection<Point> regions) {
        for (Point region : regions) {
            Ticket ticket = new Ticket(world, region);
            if (!this.ticketsToScan.contains(ticket)) {
                this.ticketsToScan.add(ticket);
            }
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
            while (!this.ticketsToScan.isEmpty()) {
                Ticket ticket = this.ticketsToScan.poll();
                if (ticket == null) continue;

                Collection<Point> set = this.regionsToScan.getOrDefault(ticket.world, new HashSet<>());
                set.add(ticket.region);
                this.regionsToScan.put(ticket.world, set);
            }

            Iterator<Map.Entry<World, Collection<Point>>> iter = this.regionsToScan.entrySet().iterator();
            while (iter.hasNext()) {
                // NEW: check for interruption BEFORE touching the next
                // world, so a deliberate stop() (e.g. during reload)
                // cleanly halts the ENTIRE remaining cycle instead of
                // limping forward into more schedule() calls against an
                // executor that may already be dead -- which previously
                // threw RejectedExecutionException from world_nether's
                // schedule() call, aborted this whole loop via the
                // catch(Throwable) below, and silently dropped every
                // world after that point (e.g. world_the_end) from the
                // scan cycle entirely.
                //
                // NOTE: entries are deliberately left un-removed from
                // regionsToScan when we bail out here, so they remain
                // queued and will be picked up again on the next run()
                // cycle instead of being lost.
                if (Thread.currentThread().isInterrupted()) {
                    // clear the flag before returning: this thread belongs
                    // to a reusable pool, and an uncleared interrupt status
                    // would otherwise leak into and spuriously abort a
                    // completely unrelated future task run on the same
                    // pooled thread.
                    Thread.interrupted();
                    Logger.debug("Region processor stopping early due to interrupt; remaining worlds will be retried next cycle.");
                    break;
                }

                Map.Entry<World, Collection<Point>> entry = iter.next();
                iter.remove();
                World world = entry.getKey();
                Collection<Point> regions = entry.getValue();
                process(world, regions);
            }
        } catch (Throwable t) {
            Logger.severe("Region processor failed to process tickets", t);
        } finally {
            this.running = false;
            Logger.debug("Region processor finished queuing at " + System.currentTimeMillis());
        }
    }

    private void process(World world, Collection<Point> regionPositions) {
        Logger.debug(world.getName() + " Region processor started processing at " + System.currentTimeMillis());

        // create spiral iterator to order region scanning
        Point spawn = world.getSpawn();
        SpiralIterator spiralIterator = new SpiralIterator(spawn.x() >> 9, spawn.z() >> 9);

        // order preserved map of regions with boolean to signify if it was already scanned
        List<Point> orderedRegionsToScan = new ArrayList<>();

        // iterate the spiral
        int totalRegions = regionPositions.size();
        int numberOfFoundRegions = 0;
        int numberOfSkippedRegions = 0;
        while (numberOfFoundRegions < totalRegions) {
            // let us not get stuck in an endless loop
            if (numberOfSkippedRegions > 1000000) {
                Logger.debug("Failsafe triggered.");
                // we scanned over a million non-existent regions straight
                // quit the spiral and add the remaining regions to the end
                orderedRegionsToScan.addAll(regionPositions);
                break;
            }

            // get region from spiral and ensure a region file exists for it
            Point regionPos = spiralIterator.next();
            if (regionPositions.remove(regionPos)) {
                // file exists, add region to scan
                orderedRegionsToScan.add(regionPos);
                numberOfFoundRegions++;
                numberOfSkippedRegions = 0;
            } else {
                numberOfSkippedRegions++;
            }
        }

        // create and send tasks to executor to run
        schedule(world, orderedRegionsToScan);

        Logger.debug(world.getName() + " Region processor finished processing at " + System.currentTimeMillis());
    }

    private void schedule(World world, List<Point> orderedRegionsToScan) {
        getProgress().setWorld(world);
        getProgress().setTotalRegions(orderedRegionsToScan.size());
        getProgress().setTotalChunks(getProgress().getTotalRegions() * 1024L);

        // NEW: the stream/allOf construction itself (specifically
        // CompletableFuture.runAsync(...) for each region task) can throw
        // RejectedExecutionException SYNCHRONOUSLY if the shared render
        // executor (Pl3xMap.api().getRenderExecutor() -- a different
        // executor than this class's own, managed elsewhere and commonly
        // shut down/recreated during a plugin reload) is not currently
        // accepting tasks. Previously this was NOT caught here, so it
        // propagated all the way up through process() into run()'s outer
        // catch(Throwable), which aborted the ENTIRE remaining scan cycle
        // -- silently dropping every world processed after this one (e.g.
        // world_the_end never got scheduled at all). Catching it locally
        // means only THIS world's tasks are skipped for now; other worlds
        // still get their chance, and this world's regions remain queued
        // for the next scan cycle.
        CompletableFuture<Void> allFutures;
        try {
            allFutures = CompletableFuture.allOf(orderedRegionsToScan.stream()
                    .map(pos -> CompletableFuture.runAsync(new RegionScanTask(world, pos), Pl3xMap.api().getRenderExecutor())
                            .whenComplete((result, throwable) -> {
                                if (throwable != null) {
                                    Logger.severe("Failed to run region scan task for %s".formatted(world.getName(), pos), throwable);
                                }

                                // set region modified time
                                world.getRegionModifiedState().set(Mathf.asLong(pos), this.timeStarted);

                                // run the garbage collector
                                if (Config.GC_WHEN_RUNNING) {
                                    System.gc();
                                }
                            })
                    ).toArray(CompletableFuture[]::new)
            );
        } catch (RejectedExecutionException e) {
            Logger.severe("Region processor could not schedule region scan tasks for world " + world.getName()
                    + " because the render executor is not currently accepting tasks (likely mid-reload). "
                    + "This world's regions remain queued and will be retried on the next scan cycle.");
            getProgress().finish();
            return;
        }

        allFutures.whenComplete((result, throwable) -> {
            if (throwable != null) {
                Logger.severe("Failed to run region scan tasks for world %s".formatted(world.getName()), throwable);
            }

            // stop the progress tracker
            getProgress().finish();

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

        try {
            allFutures.get(SCHEDULE_TIMEOUT_MINUTES, TimeUnit.MINUTES);
        } catch (InterruptedException e) {
            // NEW: caught specifically (before the generic Throwable
            // catch below) so this expected, benign consequence of a
            // deliberate stop()/reload doesn't get logged as a scary
            // unhandled failure with a full stack trace. The interrupt
            // flag is restored here (get() consumes/clears it when
            // throwing) so run()'s world-iteration loop can observe it
            // and stop processing further worlds cleanly.
            Thread.currentThread().interrupt();
            Logger.debug("Region processor's wait for world " + world.getName()
                    + " was interrupted (expected during a deliberate stop/reload).");
        } catch (TimeoutException e) {
            Logger.severe("Region processor timed out after " + SCHEDULE_TIMEOUT_MINUTES
                    + " minutes waiting for region scan tasks to finish for world " + world.getName()
                    + " -- some region files may be unusually slow to read, or a task is stuck. "
                    + "Continuing without waiting further so the processor doesn't hang permanently.", e);
        } catch (Throwable t) {
            Logger.severe("Region processor failed while waiting for region scan tasks for world " + world.getName(), t);
        }
    }

    private record Ticket(World world, Point region) {
    }
}