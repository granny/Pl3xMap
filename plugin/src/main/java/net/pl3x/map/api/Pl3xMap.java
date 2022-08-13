package net.pl3x.map.api;

import net.pl3x.map.PaletteManager;
import net.pl3x.map.api.addon.AddonManager;
import net.pl3x.map.api.httpd.IntegratedServer;
import net.pl3x.map.api.player.PlayerManager;
import net.pl3x.map.api.registry.IconRegistry;
import net.pl3x.map.render.task.RendererManager;
import net.pl3x.map.world.WorldManager;

public interface Pl3xMap {
    final class Provider {
        static Pl3xMap api = null;

        static Pl3xMap api() {
            return Provider.api;
        }
    }

    static Pl3xMap api() {
        return Provider.api;
    }

    AddonManager getAddonManager();

    IconRegistry getIconRegistry();

    IntegratedServer getIntegratedServer();

    PaletteManager getPaletteManager();

    PlayerManager getPlayerManager();

    RendererManager getRendererManager();

    WorldManager getWorldManager();
}
