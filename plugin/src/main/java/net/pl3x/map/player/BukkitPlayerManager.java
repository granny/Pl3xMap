package net.pl3x.map.player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.pl3x.map.api.player.MapPlayer;
import net.pl3x.map.api.player.PlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitPlayerManager implements PlayerManager {
    private final Map<UUID, MapPlayer> players = new HashMap<>();

    private Map<BiFunction<MapPlayer, String, String>, Integer> nameDecorators = new LinkedHashMap<>();

    public MapPlayer getPlayer(Player player) {
        UUID uuid = player.getUniqueId();
        MapPlayer mapPlayer = this.players.get(uuid);
        if (mapPlayer == null) {
            mapPlayer = new BukkitPlayer(player);
            this.players.put(uuid, mapPlayer);
        }
        return mapPlayer;
    }

    @Override
    public MapPlayer getPlayer(String name) {
        Player player = Bukkit.getPlayer(name);
        if (player == null) {
            return null;
        }
        return getPlayer(player);
    }

    @Override
    public MapPlayer getPlayer(UUID uuid) {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return null;
        }
        return getPlayer(player);
    }

    @Override
    public void unloadPlayer(UUID uuid) {
        this.players.remove(uuid);
    }

    @Override
    public void unloadAll() {
        this.players.clear();
        this.nameDecorators.clear();
    }

    @Override
    public void registerNameDecorator(int priority, BiFunction<MapPlayer, String, String> decorator) {
        nameDecorators.put(decorator, priority);

        nameDecorators = nameDecorators.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    public String decorateName(MapPlayer player) {
        String name = player.getName();
        for (BiFunction<MapPlayer, String, String> fn : nameDecorators.keySet()) {
            name = fn.apply(player, name);
        }
        return name;
    }
}
