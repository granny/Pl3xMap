package net.pl3x.map.player;

import java.util.UUID;
import net.pl3x.map.PaperPl3xMap;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class BukkitPlayer implements MapPlayer {
    private static final NamespacedKey HIDDEN_KEY = new NamespacedKey(PaperPl3xMap.getInstance(), "hidden");

    private final Player player;

    private boolean hidden;

    BukkitPlayer(Player player) {
        this.player = player;
    }

    @Override
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    @Override
    public String getName() {
        return this.player.getName();
    }

    @Override
    public boolean isHidden() {
        return this.hidden || this.player.getPersistentDataContainer().getOrDefault(HIDDEN_KEY, PersistentDataType.BYTE, (byte) 0) != 0;
    }

    @Override
    public void setHidden(boolean hidden, boolean persistent) {
        this.hidden = hidden;
        if (persistent) {
            this.player.getPersistentDataContainer().set(HIDDEN_KEY, PersistentDataType.BYTE, (byte) (hidden ? 1 : 0));
        }
    }
}
