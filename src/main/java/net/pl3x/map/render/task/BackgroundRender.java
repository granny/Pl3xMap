package net.pl3x.map.render.task;

import net.pl3x.map.world.MapWorld;

public class BackgroundRender extends AbstractRender {
    public BackgroundRender(MapWorld mapWorld) {
        super(mapWorld, true, true, true, true);
    }

    @Override
    public void run() {
    }

    @Override
    public int totalChunks() {
        return 0;
    }

    @Override
    public int totalRegions() {
        return 0;
    }
}
