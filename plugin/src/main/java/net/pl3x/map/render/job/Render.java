package net.pl3x.map.render.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.audience.Audience;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.render.job.progress.Progress;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;

public abstract class Render implements Runnable {
    private final MapWorld mapWorld;
    private final Audience starter;

    private final ExecutorService renderExecutor;
    private final ExecutorService imageExecutor;

    private final Progress progress;
    private ScheduledFuture<?> scheduledProgress;

    private final int centerX;
    private final int centerZ;

    private boolean cancelled;

    public Render(MapWorld mapWorld, Audience starter) {
        this(mapWorld, starter, mapWorld.getWorld().getSpawnLocation());
    }

    public Render(MapWorld mapWorld, Audience starter, Location loc) {
        this(mapWorld, starter, loc.getBlockX(), loc.getBlockZ());
    }

    public Render(MapWorld mapWorld, Audience starter, int centerX, int centerZ) {
        this(mapWorld, starter, centerX, centerZ,
                Executors.newFixedThreadPool(getThreads(Config.RENDER_THREADS),
                        new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Render-%d").build()),
                Executors.newFixedThreadPool(getThreads(Config.RENDER_THREADS),
                        new ThreadFactoryBuilder().setNameFormat("Pl3xMap-IO-%d").build())
        );
    }

    public Render(MapWorld mapWorld, Audience starter, int centerX, int centerZ, ExecutorService renderExecutor, ExecutorService imageExecutor) {
        this.mapWorld = mapWorld;
        this.starter = starter;
        this.progress = new Progress(this);
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.renderExecutor = renderExecutor;
        this.imageExecutor = imageExecutor;
    }

    public ExecutorService getRenderExecutor() {
        return this.renderExecutor;
    }

    public ExecutorService getImageExecutor() {
        return this.imageExecutor;
    }

    public ScheduledFuture<?> getScheduledProgress() {
        return this.scheduledProgress;
    }

    public MapWorld getWorld() {
        return this.mapWorld;
    }

    public Audience getStarter() {
        return this.starter;
    }

    public Progress getProgress() {
        return this.progress;
    }

    public int getCenterX() {
        return this.centerX;
    }

    public int getCenterZ() {
        return this.centerZ;
    }

    @Override
    public void run() {
        while (Bukkit.getCurrentTick() < 20) {
            // server is not running yet
            sleep(1000);
        }

        start();
        render();

        ThreadFactory thread = new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Progress").build();
        this.scheduledProgress = Executors.newScheduledThreadPool(1, thread)
                .scheduleAtFixedRate(getProgress(), 1L, 1L, TimeUnit.SECONDS);
    }

    public abstract void render();

    public final void start() {
        onStart();
    }

    public abstract void onStart();

    public final void finish() {
        onFinish();
    }

    public abstract void onFinish();

    public boolean isCancelled() {
        return this.cancelled;
    }

    public final void cancel(boolean unloading) {
        this.cancelled = true;

        if (this.scheduledProgress != null) {
            this.scheduledProgress.cancel(unloading);
        }

        this.renderExecutor.shutdown();
        this.imageExecutor.shutdown();

        onCancel(unloading);
    }

    public abstract void onCancel(boolean unloading);

    public static int getThreads(int threads) {
        if (threads < 1) {
            threads = Runtime.getRuntime().availableProcessors() / 2;
        }
        return Math.max(1, threads);
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
        }
    }
}
