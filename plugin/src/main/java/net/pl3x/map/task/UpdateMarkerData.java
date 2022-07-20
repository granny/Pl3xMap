package net.pl3x.map.task;

import net.pl3x.map.world.MapWorld;
import org.bukkit.scheduler.BukkitRunnable;

public class UpdateMarkerData extends BukkitRunnable {
    private final MapWorld mapWorld;

    public UpdateMarkerData(MapWorld mapWorld) {
        this.mapWorld = mapWorld;
    }

    @Override
    public void run() {
    }
}
