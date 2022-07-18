package net.pl3x.map.player;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.pl3x.map.Pl3xMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

/**
 * Manages player specific data
 */
public class PlayerManager {
    public static final PlayerManager INSTANCE = new PlayerManager();

    private final Set<UUID> hidden = new HashSet<>();
    private final NamespacedKey hiddenPDC;

    private Map<BiFunction<Player, String, String>, Integer> nameDecorators = new LinkedHashMap<>();

    public PlayerManager() {
        this.hiddenPDC = new NamespacedKey(Pl3xMap.getInstance(), "hidden");
    }

    /**
     * Check if player is hidden from the map
     *
     * @param player Player to check
     * @return True if player is hidden from the map
     */
    public boolean isHidden(Player player) {
        return this.hidden.contains(player.getUniqueId()) || getByte(player) != (byte) 0;
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
            setByte(player, (byte) (hidden ? 1 : 0));
        }
    }

    private byte getByte(Player player) {
        return player.getPersistentDataContainer().getOrDefault(this.hiddenPDC, PersistentDataType.BYTE, (byte) 0);
    }

    private void setByte(Player player, byte value) {
        player.getPersistentDataContainer().set(this.hiddenPDC, PersistentDataType.BYTE, value);
    }

    /**
     * Function that is used to change player name in a player list
     * <p>
     * Multiple decorators can be registered at the same time, in that case the one with Integer.MAX_VALUE will be run as first
     * These two values should be used only in addons that you do not plan to release for public use.
     * <p>
     * The function takes two arguments - the player and the output of previous decorator
     *
     * @param priority  Priority of decorator
     * @param decorator Name decorator to register
     */
    public void registerNameDecorator(int priority, BiFunction<Player, String, String> decorator) {
        nameDecorators.put(decorator, priority);

        nameDecorators = nameDecorators.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    public String decorateName(Player player) {
        String name = player.getName();
        for (BiFunction<Player, String, String> fn : nameDecorators.keySet()) {
            name = fn.apply(player, name);
        }
        return name;
    }
}
