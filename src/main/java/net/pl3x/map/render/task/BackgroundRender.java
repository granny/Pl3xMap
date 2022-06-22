package net.pl3x.map.render.task;

import net.kyori.adventure.audience.Audience;
import net.pl3x.map.world.MapWorld;

public class BackgroundRender extends AbstractRender {
    public BackgroundRender(MapWorld mapWorld, Audience starter) {
        super(mapWorld, "Background", starter);
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
}
