package net.pl3x.map.render.task;

import net.kyori.adventure.audience.Audience;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.render.progress.Progress;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractRender extends BukkitRunnable {
    private final MapWorld mapWorld;
    private final String type;
    private final Audience starter;
    private final Progress progress;

    private final int centerX;
    private final int centerZ;

    private boolean cancelled;

    public AbstractRender(MapWorld mapWorld, String type, Audience starter) {
        this(mapWorld, type, starter, mapWorld.getWorld().getSpawnLocation());
    }

    public AbstractRender(MapWorld mapWorld, String type, Audience starter, Location loc) {
        this(mapWorld, type, starter, loc.getBlockX(), loc.getBlockZ());
    }

    public AbstractRender(MapWorld mapWorld, String type, Audience starter, int centerX, int centerZ) {
        this.mapWorld = mapWorld;
        this.type = type;
        this.starter = starter;
        this.progress = new Progress(this);
        this.centerX = centerX;
        this.centerZ = centerZ;
    }

    public MapWorld getWorld() {
        return this.mapWorld;
    }

    public String getType() {
        return this.type;
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
    public final void run() {
        while (Bukkit.getCurrentTick() < 20) {
            // server is not running yet
            ThreadManager.sleep(1000);
        }

        start();
        render();

        getProgress().runTaskTimerAsynchronously(Pl3xMap.getInstance(), 20, 20);
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

    public final void cancel() {
        this.cancelled = true;
        onCancel();
    }

    public abstract void onCancel();
}
