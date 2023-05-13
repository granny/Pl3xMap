package net.pl3x.map.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.pl3x.map.core.network.Constants;
import net.pl3x.map.core.network.Network;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R3.map.CraftMapRenderer;
import org.bukkit.entity.Player;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class BukkitNetwork extends Network {
    private final Pl3xMapBukkit plugin;

    public BukkitNetwork(Pl3xMapBukkit plugin) {
        this.plugin = plugin;
    }

    public void register() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this.plugin, Network.CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(this.plugin, Network.CHANNEL,
                (channel, player, bytes) -> {
                    ByteArrayDataInput in = in(bytes);
                    int protocol = in.readInt();
                    if (protocol != Constants.PROTOCOL) {
                        return;
                    }
                    int action = in.readInt();
                    switch (action) {
                        case Constants.SERVER_DATA -> sendServerData(player);
                        case Constants.MAP_DATA -> sendMapData(player, in.readInt());
                    }
                }
        );
    }

    public void unregister() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(this.plugin, Network.CHANNEL);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(this.plugin, Network.CHANNEL);
    }

    protected <T> void sendMapData(T player, int id) {
        ByteArrayDataOutput out = out();

        out.writeInt(Constants.PROTOCOL);
        out.writeInt(Constants.MAP_DATA);
        out.writeInt(Constants.RESPONSE_SUCCESS);

        MapView map = map(id);
        if (map == null) {
            out.writeInt(Constants.ERROR_NO_SUCH_MAP);
            out.writeInt(id);
            return;
        }

        World world = map.getWorld();
        if (world == null) {
            out.writeInt(Constants.ERROR_NO_SUCH_WORLD);
            out.writeInt(id);
            return;
        }

        for (MapRenderer renderer : map.getRenderers()) {
            if (!renderer.getClass().getName().equals(CraftMapRenderer.class.getName())) {
                out.writeInt(Constants.ERROR_NOT_VANILLA_MAP);
                out.writeInt(id);
                return;
            }
        }

        out.writeInt(id);
        out.writeByte(getScale(map));
        out.writeInt(map.getCenterX());
        out.writeInt(map.getCenterZ());
        out.writeUTF(world.getName());

        send(player, out);
    }

    @Override
    protected <T> void send(T player, ByteArrayDataOutput out) {
        ((Player) player).sendPluginMessage(this.plugin, Network.CHANNEL, out.toByteArray());
    }

    @SuppressWarnings("deprecation")
    private MapView map(int id) {
        return Bukkit.getMap(id);
    }

    @SuppressWarnings("deprecation")
    private byte getScale(MapView map) {
        return map.getScale().getValue();
    }
}
