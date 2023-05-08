package net.pl3x.map.core.renderer.progress;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Progress implements Runnable {
    private final CPSTracker cpsTracker = new CPSTracker();

    private final AtomicLong processedChunks = new AtomicLong(0);
    private final AtomicLong processedRegions = new AtomicLong(0);

    private World world;

    private long prevProcessedChunks = 0;
    private long totalChunks;
    private long totalRegions;
    private float percent;
    private double cps;
    private String eta = Lang.PROGRESS_ETA_UNKNOWN;

    public Progress() {
        // this should run forever and ever
        Executors.newScheduledThreadPool(1).scheduleAtFixedRate(this, 1L, 1L, TimeUnit.SECONDS);
    }

    public void finish() {
        this.world = null;
        setTotalChunks(0);
        setProcessedChunks(0);
        setTotalRegions(0);
        setProcessedRegions(0);
        this.prevProcessedChunks = 0;
        this.percent = 0.0F;
        this.cps = 0.0D;
        this.eta = Lang.PROGRESS_ETA_UNKNOWN;
    }

    public @Nullable World getWorld() {
        return this.world;
    }

    public void setWorld(@Nullable World world) {
        this.world = world;
    }

    public long getTotalChunks() {
        return this.totalChunks;
    }

    public void setTotalChunks(long totalChunks) {
        this.totalChunks = totalChunks;
    }

    public long getTotalRegions() {
        return this.totalRegions;
    }

    public void setTotalRegions(long totalRegions) {
        this.totalRegions = totalRegions;
    }

    public void increment() {
        this.processedRegions.set(this.processedRegions.get() + 1);
        this.processedChunks.set(this.processedChunks.get() + 1024);
    }

    public float getPercent() {
        return this.percent;
    }

    public double getCPS() {
        return this.cps;
    }

    public @NonNull String getETA() {
        return this.eta;
    }

    public @NonNull AtomicLong getProcessedChunks() {
        return this.processedChunks;
    }

    public void setProcessedChunks(long processedChunks) {
        getProcessedChunks().set(processedChunks);
        this.prevProcessedChunks = processedChunks;
    }

    public @NonNull AtomicLong getProcessedRegions() {
        return this.processedRegions;
    }

    public void setProcessedRegions(long processedRegions) {
        getProcessedRegions().set(processedRegions);
    }

    @Override
    public void run() {
        try {
            runProgress();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    private void runProgress() {
        if (this.world == null || Pl3xMap.api().getRegionProcessor().isPaused()) {
            return;
        }

        long processedChunks = getProcessedChunks().get();
        this.cpsTracker.add(processedChunks - this.prevProcessedChunks);
        this.prevProcessedChunks = processedChunks;
        this.percent = ((float) processedChunks / (float) getTotalChunks()) * 100.0F;
        this.cps = this.cpsTracker.average();
        if (this.cps > 0.0D) {
            long timeLeft = (this.totalChunks - processedChunks) / (long) this.cps * 1000L;
            this.eta = formatMilliseconds(timeLeft);
        } else {
            this.eta = Lang.PROGRESS_ETA_UNKNOWN;
        }
    }

    public static @NonNull String formatMilliseconds(long time) {
        int hrs = (int) TimeUnit.MILLISECONDS.toHours(time);
        int min = (int) TimeUnit.MILLISECONDS.toMinutes(time) % 60;
        int sec = (int) TimeUnit.MILLISECONDS.toSeconds(time) % 60;
        if (hrs > 0) {
            return String.format("%dh %dm %ds", hrs, min, sec);
        } else if (min > 0) {
            return String.format("%dm %ds", min, sec);
        } else {
            return String.format("%ds", sec);
        }
    }
}
