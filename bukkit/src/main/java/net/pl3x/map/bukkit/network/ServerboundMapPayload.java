package net.pl3x.map.bukkit.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.pl3x.map.core.network.Constants;
import org.jspecify.annotations.NullMarked;

@NullMarked
public record ServerboundMapPayload(int protocol, int mapId) implements CustomPacketPayload {
    public static final StreamCodec<FriendlyByteBuf, ServerboundMapPayload> STREAM_CODEC = CustomPacketPayload.codec(ServerboundMapPayload::write, ServerboundMapPayload::new);
    public static final Type<ServerboundMapPayload> TYPE = new Type<>(Identifier.fromNamespaceAndPath(Constants.MODID, "server_map_data"));

    public ServerboundMapPayload(int mapId) {
        this(Constants.PROTOCOL, mapId);
    }

    public ServerboundMapPayload(FriendlyByteBuf friendlyByteBuf) {
        this(friendlyByteBuf.readInt(), friendlyByteBuf.readInt());
    }

    private void write(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(protocol);
        friendlyByteBuf.writeInt(mapId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
