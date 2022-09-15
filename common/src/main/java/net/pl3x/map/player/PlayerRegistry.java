package net.pl3x.map.player;

import java.util.Locale;
import java.util.UUID;
import net.pl3x.map.registry.KeyedRegistry;
import org.jetbrains.annotations.Nullable;

/**
 * Manages player specific data
 */
public abstract class PlayerRegistry extends KeyedRegistry<Player> {
    /**
     * Get the registered player by uuid.
     * <p>
     * Will return null if no player registered.
     *
     * @param uuid player uuid
     * @return registered player or null
     */
    @Nullable
    public Player get(UUID uuid) {
        return get(Player.createKey(uuid));
    }

    /**
     * Get the registered player by name.
     * <p>
     * Will return null if no player registered.
     *
     * @param name player name
     * @return registered player or null
     */
    @Nullable
    public Player get(String name) {
        String lowercaseName = name.toLowerCase(Locale.ROOT);
        for (Player player : entries().values()) {
            if (player.getName().toLowerCase(Locale.ROOT).equals(lowercaseName)) {
                return player;
            }
        }
        return null;
    }
}
