package net.pl3x.map.fabric.mixin;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.player.PlayerListener;
import net.pl3x.map.core.player.PlayerRegistry;
import net.pl3x.map.fabric.FabricPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(PlayerList.class)
public class MixinPlayerList {
    private final PlayerListener playerListener = new PlayerListener() {
    };

    @Inject(method = "placeNewPlayer", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/protocol/game/ClientboundCustomPayloadPacket;<init>(Lnet/minecraft/resources/ResourceLocation;Lnet/minecraft/network/FriendlyByteBuf;)V"))
    private void placeNewPlayer(Connection connection, ServerPlayer player, CallbackInfo info) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        this.playerListener.onJoin(registry.register(player.getUUID().toString(), new FabricPlayer(player)));
    }

    @Inject(method = "remove", at = @At(value = "HEAD"))
    private void remove(ServerPlayer player, CallbackInfo info) {
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();
        this.playerListener.onQuit(registry.unregister(player.getUUID().toString()));
    }
}
