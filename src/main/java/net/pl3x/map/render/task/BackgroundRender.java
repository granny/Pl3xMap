package net.pl3x.map.render.task;

import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;

public class BackgroundRender extends AbstractRender {
    public BackgroundRender(MapWorld mapWorld) {
        super(mapWorld, "Background", Bukkit.getConsoleSender());
    }

    @Override
    public void render() {
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onFinish() {
    }

    @Override
    public void onCancel() {
    }

    public void reset() {
        // todo - cancel all current jobs
        // todo - cancel all pending jobs
        // todo - wipe dirty_chunks.yml
    }
}
