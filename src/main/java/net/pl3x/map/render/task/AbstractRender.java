package net.pl3x.map.render.task;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.progress.Progress;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

public abstract class AbstractRender extends BukkitRunnable {
    private final MapWorld mapWorld;
    private final String type;
    private final Progress progress;

    private final boolean renderBlocks;
    private final boolean renderBiomes;
    private final boolean renderHeights;
    private final boolean renderFluids;

    private int centerX;
    private int centerZ;

    private boolean cancelled;

    public AbstractRender(MapWorld mapWorld, String type, boolean renderBlocks, boolean renderBiomes, boolean renderHeights, boolean renderFluids) {
        this.mapWorld = mapWorld;
        this.type = type;
        this.progress = new Progress(this);

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

    public String getType() {
        return this.type;
    }

    public Progress getProgress() {
        return this.progress;
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

    public abstract void render();

    @Override
    public void run() {
        render();

        getProgress().runTaskTimerAsynchronously(Pl3xMap.getInstance(), 20, 20);
    }
}
