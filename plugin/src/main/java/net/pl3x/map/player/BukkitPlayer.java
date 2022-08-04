package net.pl3x.map.player;

import java.util.UUID;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.api.player.MapPlayer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

public class BukkitPlayer implements MapPlayer {
    private static final NamespacedKey HIDDEN_KEY = new NamespacedKey(Pl3xMapPlugin.getInstance(), "hidden");

    private final Player player;

    private boolean hidden;

    BukkitPlayer(Player player) {
        this.player = player;
    }

    @Override
    public UUID getUUID() {
        return null;
    }

    @Override
    public String getName() {
        return null;
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
