package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.player.MapPlayer;
import net.pl3x.map.player.PlayerRegistry;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;

public class UpdatePlayerData implements Runnable {
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    @Override
    public void run() {
        List<Object> players = new ArrayList<>();

        PlayerRegistry playerRegistry = Pl3xMap.api().getPlayerRegistry();

        Pl3xMap.api().getWorldRegistry().entries().forEach((key, mapWorld) -> {
            mapWorld.getWorld().getLevel().players().forEach(player -> {
                if (mapWorld.getConfig().PLAYER_TRACKER_HIDE_SPECTATORS && playerRegistry.isSpectator(player)) {
                    return;
                }
                if (mapWorld.getConfig().PLAYER_TRACKER_HIDE_INVISIBLE && player.isInvisible()) {
                    return;
                }
                if (playerRegistry.isNPC(player)) {
                    return;
                }
                MapPlayer mapPlayer = playerRegistry.getPlayer(player.getUUID());
                if (mapPlayer.isHidden()) {
                    return;
                }
                Map<String, Object> playerEntry = new LinkedHashMap<>();
                BlockPos pos = player.blockPosition();

                playerEntry.put("name", playerRegistry.decorateName(mapPlayer));
                playerEntry.put("uuid", player.getUUID().toString().replace("-", ""));
                playerEntry.put("world", mapWorld.getWorld().getName());

                if (mapWorld.getConfig().PLAYER_TRACKER_ENABLED) {
                    playerEntry.put("x", pos.getX());
                    playerEntry.put("z", pos.getZ());
                    playerEntry.put("yaw", player.getYHeadRot());
                    if (mapWorld.getConfig().PLAYER_TRACKER_NAMEPLATE_SHOW_ARMOR) {
                        playerEntry.put("armor", getArmorPoints(player));
                    }
                    if (mapWorld.getConfig().PLAYER_TRACKER_NAMEPLATE_SHOW_HEALTH) {
                        playerEntry.put("health", (int) player.getHealth());
                    }
                }

                players.add(playerEntry);
            });
        });

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("max", MinecraftServer.getServer().getMaxPlayers());
        map.put("players", players);

        FileUtil.write(this.gson.toJson(map), MapWorld.TILES_DIR.resolve("players.json"));
    }

    private static int getArmorPoints(ServerPlayer player) {
        AttributeInstance attr = player.getAttribute(Attributes.ARMOR);
        return attr == null ? 0 : (int) attr.getValue();
    }
}
