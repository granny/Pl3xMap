package net.pl3x.map.render.job;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.Sender;
import net.pl3x.map.markers.Point;
import net.pl3x.map.render.job.progress.Progress;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.Mathf;
import net.pl3x.map.world.World;

public abstract class Render implements Runnable {
    private final World world;
    private final Sender starter;

    private final ExecutorService renderExecutor;
    private final ExecutorService imageExecutor;

    private final Progress progress;
    private ScheduledFuture<?> scheduledProgress;

    private final int centerX;
    private final int centerZ;

    private final BiomeColors biomeColors;

    private boolean cancelled;

    public Render(World world, Sender starter) {
        this(world, starter, world.getSpawn());
    }

    public Render(World world, Sender starter, Point spawn) {
        this(world, starter, spawn.getX(), spawn.getZ());
    }

    public Render(World world, Sender starter, int centerX, int centerZ) {
        this(world, starter, centerX, centerZ,
                Executors.newFixedThreadPool(getThreads(world.getConfig().RENDER_THREADS),
                        new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Render-%d").build()),
                Executors.newFixedThreadPool(getThreads(world.getConfig().RENDER_THREADS),
                        new ThreadFactoryBuilder().setNameFormat("Pl3xMap-IO-%d").build())
        );
    }

    public Render(World world, Sender starter, int centerX, int centerZ, ExecutorService renderExecutor, ExecutorService imageExecutor) {
        this.world = world;
        this.starter = starter;
        this.progress = new Progress(this);
        this.centerX = centerX;
        this.centerZ = centerZ;
        this.renderExecutor = renderExecutor;
        this.imageExecutor = imageExecutor;
        this.biomeColors = new BiomeColors(world);
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

    public World getWorld() {
        return this.world;
    }

    public Sender getStarter() {
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

    public BiomeColors getBiomeColors() {
        return this.biomeColors;
    }

    @Override
    public void run() {
        try {
            while (Pl3xMap.api().getCurrentTick() < 20) {
                // server is not running yet
                sleep(1000);
            }
            start();
            render();
        } catch (Throwable t) {
            t.printStackTrace();
            cancel(false);
        }

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

        getProgress().getBossbar().hideAll();

        onCancel(unloading);
    }

    public abstract void onCancel(boolean unloading);

    public static int getThreads(int threads) {
        int max = Runtime.getRuntime().availableProcessors() / 2;
        return Mathf.clamp(1, max, Math.max(threads, max));
    }

    public void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ignore) {
        }
    }
}
