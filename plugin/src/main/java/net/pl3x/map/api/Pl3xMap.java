package net.pl3x.map.api;

import net.pl3x.map.PaletteManager;
import net.pl3x.map.api.addon.AddonManager;
import net.pl3x.map.api.heightmap.HeightmapRegistry;
import net.pl3x.map.api.httpd.IntegratedServer;
import net.pl3x.map.api.player.PlayerManager;
import net.pl3x.map.api.registry.EventRegistry;
import net.pl3x.map.api.registry.IconRegistry;
import net.pl3x.map.api.registry.LayerRegistry;
import net.pl3x.map.render.task.RendererRegistry;
import net.pl3x.map.world.WorldManager;

public interface Pl3xMap {
    final class Provider {
        static Pl3xMap api = null;

        public static Pl3xMap api() {
            return Provider.api;
        }
    }

    static Pl3xMap api() {
        return Provider.api();
    }

    AddonManager getAddonManager();

    EventRegistry getEventRegistry();

    HeightmapRegistry getHeightmapRegistry();

    IconRegistry getIconRegistry();

    LayerRegistry getLayerRegistry();

    IntegratedServer getIntegratedServer();

    PaletteManager getPaletteManager();

    PlayerManager getPlayerManager();

    RendererRegistry getRendererRegistry();

    WorldManager getWorldManager();
}
