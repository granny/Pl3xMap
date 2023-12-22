/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.bukkit;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import net.pl3x.map.core.network.Constants;
import net.pl3x.map.core.network.Network;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_20_R3.map.CraftMapRenderer;
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

        MapView map = Bukkit.getMap(id);
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
    private byte getScale(MapView map) {
        return map.getScale().getValue();
    }
}
