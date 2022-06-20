package net.pl3x.map.render.task;

import net.pl3x.map.world.MapWorld;

public class BackgroundRender extends AbstractRender {
    public BackgroundRender(MapWorld mapWorld) {
        super(mapWorld, "Background", true, true, true, true);
    }

    @Override
    public void render() {
    }
}
