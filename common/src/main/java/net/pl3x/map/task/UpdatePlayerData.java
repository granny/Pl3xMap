package net.pl3x.map.task;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.server.MinecraftServer;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;

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

        Pl3xMap.api().getPlayerRegistry().entries().forEach((key, player) -> {
            if (player.isHidden()) {
                return;
            }

            if (player.isNPC()) {
                return;
            }

            Map<String, Object> entry = new LinkedHashMap<>();

            entry.put("name", player.getDecoratedName());
            entry.put("uuid", player.getUUID().toString());
            entry.put("world", player.getWorld().getName());

            players.add(entry);
        });

        Map<String, Object> map = new LinkedHashMap<>();
        map.put("max", MinecraftServer.getServer().getMaxPlayers());
        map.put("players", players);

        FileUtil.write(this.gson.toJson(map), World.TILES_DIR.resolve("players.json"));
    }
}
