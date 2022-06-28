package net.pl3x.map.render;

import net.pl3x.map.logger.Logger;
import net.pl3x.map.world.MapWorld;
import org.bukkit.Bukkit;

public class BackgroundRender extends AbstractRender {
    public BackgroundRender(MapWorld mapWorld) {
        super(mapWorld, Bukkit.getConsoleSender());
    }

    @Override
    public void run() {
        while (Bukkit.getCurrentTick() < 20) {
            // server is not running yet
            sleep(1000);
        }
        render();
    }

    @Override
    public void render() {
        // just some temp output to make sure things are ticking right
        Logger.debug("Test Background Render... " + getWorld().getName() + " - " + System.currentTimeMillis());
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
