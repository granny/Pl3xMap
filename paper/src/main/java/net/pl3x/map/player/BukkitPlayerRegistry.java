package net.pl3x.map.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlayerRegistry extends PlayerRegistry {
    @NotNull
    public Player register(@NotNull org.bukkit.entity.Player player) {
        return register(new BukkitPlayer(player));
    }

    @Nullable
    public Player unregister(@NotNull org.bukkit.entity.Player player) {
        return unregister(player.getName());
    }

    @Nullable
    public Player get(@NotNull org.bukkit.entity.Player player) {
        return get(player.getName());
    }
}
