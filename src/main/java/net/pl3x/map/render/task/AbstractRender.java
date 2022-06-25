package net.pl3x.map.render.task;

import net.kyori.adventure.audience.Audience;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.render.progress.Progress;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractRender extends BukkitRunnable {
    private final MapWorld mapWorld;
    private final String type;
    private final Audience starter;
    private final Progress progress;

    private int centerX;
    private int centerZ;

    private boolean cancelled;

    public AbstractRender(MapWorld mapWorld, String type, Audience starter) {
        this.mapWorld = mapWorld;
        this.type = type;
        this.starter = starter;
        this.progress = new Progress(this);

        Location spawn = this.mapWorld.getWorld().getSpawnLocation();
        setCenterX(spawn.getBlockX());
        setCenterZ(spawn.getBlockZ());
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

    public void setCenterX(int x) {
        this.centerX = x;
    }

    public int getCenterZ() {
        return this.centerZ;
    }

    public void setCenterZ(int z) {
        this.centerZ = z;
    }

    @Override
    public final void run() {
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
