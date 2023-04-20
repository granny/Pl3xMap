package net.pl3x.map.fabric.mixin;

import java.util.function.BooleanSupplier;
import net.minecraft.server.MinecraftServer;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.fabric.Pl3xMapImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings("unused")
@Mixin(MinecraftServer.class)
public class MixinMinecraftServer {
    @Inject(method = "tickServer", at = @At("TAIL"))
    private void tickServer(BooleanSupplier shouldKeepTicking, CallbackInfo info) {
        Pl3xMap.api().getScheduler().tick();
    }

    @SuppressWarnings("DataFlowIssue")
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;", ordinal = 0))
    private void runServer(CallbackInfo info) {
        ((Pl3xMapImpl) Pl3xMap.api()).enable((MinecraftServer) (Object) this);
    }

    @Inject(method = "stopServer", at = @At("HEAD"))
    private void stopServer(CallbackInfo info) {
        Pl3xMap.api().disable();
        Pl3xMap.api().getBlockRegistry().unregister();
    }
}
