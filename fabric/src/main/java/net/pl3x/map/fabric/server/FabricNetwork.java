package net.pl3x.map.fabric.server;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.pl3x.map.core.network.Constants;
import net.pl3x.map.core.network.Network;

public class FabricNetwork extends Network {
    private final Pl3xMapFabricServer mod;
    private final ResourceLocation channel;

    public FabricNetwork(Pl3xMapFabricServer mod) {
        this.mod = mod;
        this.channel = new ResourceLocation(Network.CHANNEL);
    }

    @Override
    public void register() {
        ServerPlayNetworking.registerGlobalReceiver(this.channel, (server, player, listener, byteBuf, sender) -> {
            ByteArrayDataInput in = in(byteBuf.accessByteBufWithCorrectSize());
            int action = in.readInt();
            switch (action) {
                case Constants.SERVER_DATA -> sendServerData(player);
                case Constants.MAP_DATA -> sendMapData(player, in.readInt());
            }
        });
    }

    @Override
    public void unregister() {
        ServerPlayNetworking.unregisterGlobalReceiver(this.channel);
    }

    @Override
    protected <T> void sendMapData(T player, int id) {
        ByteArrayDataOutput out = out();

        out.writeInt(Constants.PROTOCOL);
        out.writeInt(Constants.MAP_DATA);
        out.writeInt(Constants.RESPONSE_SUCCESS);

        MinecraftServer server = this.mod.getServer();
        if (server == null) {
            return;
        }

        @SuppressWarnings("DataFlowIssue")
        MapItemSavedData map = MapItem.getSavedData(id, server.getLevel(Level.OVERWORLD));
        if (map == null) {
            out.writeInt(Constants.ERROR_NO_SUCH_MAP);
            out.writeInt(id);
            return;
        }

        ServerLevel level = this.mod.getServer().getLevel(map.dimension);
        if (level == null) {
            out.writeInt(Constants.ERROR_NO_SUCH_WORLD);
            out.writeInt(id);
            return;
        }

        out.writeInt(id);
        out.writeByte(map.scale);
        out.writeInt(map.centerX);
        out.writeInt(map.centerZ);
        out.writeUTF(level.dimension().location().toString());

        send(player, out);
    }

    @Override
    protected <T> void send(T player, ByteArrayDataOutput out) {
        FriendlyByteBuf byteBuf = new FriendlyByteBuf(Unpooled.wrappedBuffer(out.toByteArray()));
        ((ServerPlayer) player).connection.send(new ClientboundCustomPayloadPacket(this.channel, byteBuf));
    }
}
