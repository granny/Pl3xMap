package net.pl3x.map.forge;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import io.netty.buffer.Unpooled;
import java.util.function.Consumer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundCustomPayloadPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.event.EventNetworkChannel;
import net.pl3x.map.core.network.Constants;
import net.pl3x.map.core.network.Network;

public class ForgeNetwork extends Network {
    private final Pl3xMapForge mod;
    private final ResourceLocation channel;
    private final EventNetworkChannel network;

    private Consumer<NetworkEvent.ClientCustomPayloadEvent> listener;

    public ForgeNetwork(Pl3xMapForge mod) {
        this.mod = mod;
        this.channel = new ResourceLocation(Network.CHANNEL);

        String protocol = String.valueOf(Constants.PROTOCOL);
        this.network = NetworkRegistry.newEventChannel(this.channel, () -> protocol, v -> true, v -> true);
    }

    @Override
    public void register() {
        this.listener = (NetworkEvent.ClientCustomPayloadEvent event) -> {
            ServerPlayer player = event.getSource().get().getSender();
            if (player == null) {
                return;
            }
            event.getSource().get().setPacketHandled(true);
            ByteArrayDataInput in = in(event.getPayload().accessByteBufWithCorrectSize());
            int action = in.readInt();
            switch (action) {
                case Constants.SERVER_DATA -> sendServerData(player);
                case Constants.MAP_DATA -> sendMapData(player, in.readInt());
            }
        };


        this.network.addListener(this.listener);
    }

    @Override
    public void unregister() {
        this.network.unregisterObject(this.listener);
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
