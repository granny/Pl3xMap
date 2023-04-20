package net.pl3x.map.core.player;

import java.util.Locale;
import java.util.UUID;
import net.pl3x.map.core.registry.Registry;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages player specific data
 */
public class PlayerRegistry extends Registry<Player> {
    /**
     * Get the registered player by uuid.
     * <p>
     * Will return null if no player registered.
     *
     * @param uuid player uuid
     * @return registered player or null
     */
    @Nullable
    public Player get(@NonNull UUID uuid) {
        return super.get(uuid.toString());
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
    public Player get(@NonNull String name) {
        String lowercaseName = name.toLowerCase(Locale.ROOT);
        for (Player player : values()) {
            if (player.getName().toLowerCase(Locale.ROOT).equals(lowercaseName)) {
                return player;
            }
        }
        return null;
    }
}
