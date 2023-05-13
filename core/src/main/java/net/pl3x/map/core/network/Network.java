package net.pl3x.map.core.network;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.pl3x.map.core.configuration.Config;

public abstract class Network {
    public static final String CHANNEL = Constants.MODID + ":" + Constants.MODID;

    public abstract void register();

    public abstract void unregister();

    public <T> void sendServerData(T player) {
        ByteArrayDataOutput out = out();

        out.writeInt(Constants.PROTOCOL);
        out.writeInt(Constants.SERVER_DATA);
        out.writeInt(Constants.RESPONSE_SUCCESS);

        out.writeUTF(Config.WEB_ADDRESS);

        send(player, out);
    }

    protected abstract <T> void sendMapData(T player, int id);

    protected abstract <T> void send(T player, ByteArrayDataOutput out);

    @SuppressWarnings("UnstableApiUsage")
    protected ByteArrayDataOutput out() {
        return ByteStreams.newDataOutput();
    }

    @SuppressWarnings("UnstableApiUsage")
    protected ByteArrayDataInput in(byte[] bytes) {
        return ByteStreams.newDataInput(bytes);
    }
}
