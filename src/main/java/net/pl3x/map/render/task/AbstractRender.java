package net.pl3x.map.render.task;

import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractRender extends BukkitRunnable {
    private final MapWorld mapWorld;

    private final boolean renderBlocks;
    private final boolean renderBiomes;
    private final boolean renderHeights;
    private final boolean renderFluids;

    final AtomicInteger curChunks = new AtomicInteger(0);
    final AtomicInteger curRegions = new AtomicInteger(0);

    protected BukkitTask timer;

    private int centerX;
    private int centerZ;

    public AtomicInteger finishedChunks = new AtomicInteger();
    protected CPS cps = new CPS();

    private boolean cancelled;

    public AbstractRender(MapWorld mapWorld, boolean renderBlocks, boolean renderBiomes, boolean renderHeights, boolean renderFluids) {
        this.mapWorld = mapWorld;

        this.renderBlocks = renderBlocks;
        this.renderBiomes = renderBiomes;
        this.renderHeights = renderHeights;
        this.renderFluids = renderFluids;

        Location spawn = this.mapWorld.getWorld().getSpawnLocation();
        setCenterX(spawn.getBlockX());
        setCenterZ(spawn.getBlockZ());
    }

    public MapWorld getWorld() {
        return this.mapWorld;
    }

    public boolean renderBlocks() {
        return renderBlocks;
    }

    public boolean renderBiomes() {
        return renderBiomes;
    }

    public boolean renderHeights() {
        return renderHeights;
    }

    public boolean renderFluids() {
        return renderFluids;
    }

    public int getCenterX() {
        return this.centerX;
    }

    public void setCenterX(int x) {
        this.centerX = x;
    }

    public int getCenterZ() {
        return this.centerZ;
    }

    public void setCenterZ(int z) {
        this.centerZ = z;
    }

    public void cancel() {
        this.cancelled = true;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

    public abstract int totalChunks();

    public final int processedChunks() {
        return this.curChunks.get();
    }

    public abstract int totalRegions();

    public final int processedRegions() {
        return this.curRegions.get();
    }

    public void scanRegion(RegionCoordinate key) {
    }

    public static class CPS {
        final int[] avg = new int[15];
        int index = 0;

        public void add(int val) {
            index++;
            if (index == 15) {
                index = 0;
            }
            avg[index] = val;
        }

        public double average() {
            return Arrays.stream(avg).filter(i -> i != 0).average().orElse(0.00D);
        }
    }

    public class TempTimer extends BukkitRunnable {
        int prev = 0;

        @Override
        public void run() {
            int cur = finishedChunks.get();
            int diff = cur - this.prev;
            this.prev = cur;

            cps.add(diff);

            ThreadPoolExecutor saveExecutor = (ThreadPoolExecutor) ThreadManager.INSTANCE.getSaveExecutor();
            ThreadPoolExecutor renderExecutor = (ThreadPoolExecutor) ThreadManager.INSTANCE.getRenderExecutor();

            long save = saveExecutor.getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count();
            long render = renderExecutor.getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count();

            Logger.debug("Progress: " + cur + "/" + totalChunks() + " (" + String.format("%.2f%%", ((float) cur / (float) totalChunks()) * 100.0F) + ") " + String.format("<gold>%.2f", cps.average()) + " cps</gold> (" + save + "," + render + ")");

            // TODO - this finishing needs some work..
            if (render <= 0) {
                cur = finishedChunks.get();
                save = saveExecutor.getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count();
                render = renderExecutor.getQueue().stream().filter(t -> !((FutureTask<?>) t).isDone()).count();
                Logger.debug("<dark_aqua>Finished: " + cur + "/" + totalChunks() + " (" + String.format("%.2f%%", ((float) cur / (float) totalChunks()) * 100.0F) + ") " + String.format("<gold>%.2f", cps.average()) + " cps</gold> (" + save + "," + render + ")");
                cancel();
            }
        }
    }
}
