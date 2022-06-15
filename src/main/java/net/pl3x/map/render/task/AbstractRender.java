package net.pl3x.map.render.task;

import net.pl3x.map.render.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractRender extends BukkitRunnable {
    final MapWorld mapWorld;

    final AtomicInteger curChunks = new AtomicInteger(0);
    final AtomicInteger curRegions = new AtomicInteger(0);

    private int centerX;
    private int centerZ;

    private boolean cancelled;

    public AbstractRender(MapWorld mapWorld) {
        this.mapWorld = mapWorld;

        Location spawn = this.mapWorld.getWorld().getSpawnLocation();
        setCenterX(spawn.getBlockX());
        setCenterZ(spawn.getBlockZ());
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

    public interface Queue {
        void run();
    }
}
