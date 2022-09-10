package net.pl3x.map.player;

import net.pl3x.map.Registry;
import org.jetbrains.annotations.Nullable;

/**
 * Manages player specific data
 */
public abstract class PlayerRegistry extends Registry<Player> {
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
        return get(Player.createKey(name));
    }
}
