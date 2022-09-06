package net.pl3x.map;

import java.nio.file.Path;
import net.kyori.adventure.audience.Audience;
import net.pl3x.map.addon.AddonRegistry;
import net.pl3x.map.event.EventRegistry;
import net.pl3x.map.heightmap.HeightmapRegistry;
import net.pl3x.map.httpd.IntegratedServer;
import net.pl3x.map.image.IconRegistry;
import net.pl3x.map.palette.PaletteRegistry;
import net.pl3x.map.player.PlayerRegistry;
import net.pl3x.map.render.RendererRegistry;
import net.pl3x.map.world.WorldRegistry;
import org.jetbrains.annotations.NotNull;

public interface Pl3xMap {
    final class Provider {
        static Pl3xMap api = null;

        @NotNull
        public static Pl3xMap api() {
            return Provider.api;
        }
    }

    @NotNull
    static Pl3xMap api() {
        return Provider.api();
    }

    int getCurrentTick();

    @NotNull
    Path getMainDir();

    void saveResource(@NotNull String resourcePath, boolean replace);

    @NotNull
    Audience getConsole();

    AddonRegistry getAddonRegistry();

    EventRegistry getEventRegistry();

    HeightmapRegistry getHeightmapRegistry();

    IconRegistry getIconRegistry();

    IntegratedServer getIntegratedServer();

    PaletteRegistry getPaletteRegistry();

    PlayerRegistry getPlayerRegistry();

    RendererRegistry getRendererRegistry();

    WorldRegistry getWorldRegistry();
}
