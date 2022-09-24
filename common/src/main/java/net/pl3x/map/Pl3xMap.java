package net.pl3x.map;

import java.nio.file.Path;
import net.pl3x.map.addon.AddonRegistry;
import net.pl3x.map.command.Console;
import net.pl3x.map.event.EventRegistry;
import net.pl3x.map.heightmap.HeightmapRegistry;
import net.pl3x.map.httpd.IntegratedServer;
import net.pl3x.map.image.IconRegistry;
import net.pl3x.map.palette.BlockPaletteRegistry;
import net.pl3x.map.player.PlayerListener;
import net.pl3x.map.player.PlayerRegistry;
import net.pl3x.map.render.RendererRegistry;
import net.pl3x.map.world.WorldListener;
import net.pl3x.map.world.WorldRegistry;
import org.jetbrains.annotations.NotNull;

public interface Pl3xMap {
    final class Provider {
        static Pl3xMap api;

        @NotNull
        public static Pl3xMap api() {
            return Provider.api;
        }
    }

    @NotNull
    static Pl3xMap api() {
        return Provider.api();
    }

    void enable();

    void disable();

    @NotNull
    Impl getImpl();

    @NotNull
    String getVersion();

    int getCurrentTick();

    @NotNull
    Path getMainDir();

    @NotNull
    Console getConsole();

    @NotNull
    PlayerListener getPlayerListener();

    @NotNull
    WorldListener getWorldListener();

    @NotNull
    IntegratedServer getIntegratedServer();

    @NotNull
    AddonRegistry getAddonRegistry();

    @NotNull
    EventRegistry getEventRegistry();

    @NotNull
    HeightmapRegistry getHeightmapRegistry();

    @NotNull
    IconRegistry getIconRegistry();

    @NotNull
    BlockPaletteRegistry getBlockPaletteRegistry();

    @NotNull
    PlayerRegistry getPlayerRegistry();

    @NotNull
    RendererRegistry getRendererRegistry();

    @NotNull
    WorldRegistry getWorldRegistry();

    enum Impl {
        PAPER;
    }
}
