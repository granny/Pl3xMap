package net.pl3x.map.forge;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.player.PlayerRegistry;
import org.checkerframework.checker.nullness.qual.NonNull;

@Mod("pl3xmap")
public class Pl3xMapForge {
    private final Pl3xMap pl3xmap;

    private MinecraftServer server;
    private final PlayerListener playerListener;

    public Pl3xMapForge() {
        this.pl3xmap = new Pl3xMapImpl(this);

        this.playerListener = new PlayerListener() {
        };

        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.@NonNull ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            this.pl3xmap.getScheduler().tick();
        }
    }

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerEvent.@NonNull PlayerLoggedInEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        this.playerListener.onJoin(registry.register(event.getEntity().getUUID().toString(), new ForgePlayer(event.getEntity())));
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerEvent.@NonNull PlayerLoggedOutEvent event) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        this.playerListener.onQuit(registry.unregister(event.getEntity().getUUID().toString()));
    }

    @SubscribeEvent
    public void onServerStarted(@NonNull ServerStartedEvent event) {
        this.server = event.getServer();
        this.pl3xmap.enable();
    }

    @SubscribeEvent
    public void onServerStopping(@NonNull ServerStoppingEvent event) {
        this.pl3xmap.disable();
        this.pl3xmap.getBlockRegistry().unregister();
    }

    @NonNull
    public MinecraftServer getServer() {
        return this.server;
    }
}
