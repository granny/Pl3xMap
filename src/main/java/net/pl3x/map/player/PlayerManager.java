package net.pl3x.map.player;

import net.pl3x.map.Pl3xMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Manages player specific data
 */
public class PlayerManager {
    private final Set<UUID> hidden = new HashSet<>();
    private final NamespacedKey hiddenPDC;

    public PlayerManager(Pl3xMap plugin) {
        this.hiddenPDC = new NamespacedKey(plugin, "hidden");
    }

    /**
     * Check if player is hidden from the map
     *
     * @param player Player to check
     * @return True if player is hidden from the map
     */
    public boolean isHidden(Player player) {
        return this.hidden.contains(player.getUniqueId()) || getByte(player, hiddenPDC) != (byte) 0;
    }

    /**
     * Set if a player is hidden from the map
     *
     * @param player     Player to set
     * @param hidden     True to hide, false to show
     * @param persistent True to persist this state
     */
    public void setHidden(Player player, boolean hidden, boolean persistent) {
        if (hidden) {
            this.hidden.add(player.getUniqueId());
        } else {
            this.hidden.remove(player.getUniqueId());
        }
        if (persistent) {
            setByte(player, hiddenPDC, (byte) (hidden ? 1 : 0));
        }
    }

    private byte getByte(Player player, NamespacedKey key) {
        return player.getPersistentDataContainer().getOrDefault(key, PersistentDataType.BYTE, (byte) 0);
    }

    private void setByte(Player player, NamespacedKey key, byte value) {
        player.getPersistentDataContainer().set(key, PersistentDataType.BYTE, value);
    }
}
