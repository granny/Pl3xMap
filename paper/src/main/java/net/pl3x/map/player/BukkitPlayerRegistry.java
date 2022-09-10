package net.pl3x.map.player;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlayerRegistry extends PlayerRegistry {
    /**
     * Register a new player.
     * <p>
     * Will return null if a player is already registered.
     *
     * @param player bukkit player to register
     * @return registered player or null
     */
    @Nullable
    public Player register(@NotNull org.bukkit.entity.Player player) {
        return register(new BukkitPlayer(player));
    }

    /**
     * Unregister the specified player.
     * <p>
     * Will return null if player is not registered.
     *
     * @param player bukkit player to unregister
     * @return unregistered player or null
     */
    @Nullable
    public Player unregister(@NotNull org.bukkit.entity.Player player) {
        Player p = get(player.getName());
        return p == null ? null : unregister(p);
    }

    /**
     * Get the registered player for the provided key.
     * <p>
     * Will return null if no player registered with provided key.
     *
     * @param player bukkit player
     * @return registered player or null
     */
    @Nullable
    public Player get(@NotNull org.bukkit.entity.Player player) {
        return get(player.getName());
    }
}
