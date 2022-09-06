package net.pl3x.map.player;

import java.util.UUID;

public interface MapPlayer {
    UUID getUUID();

    String getName();

    /**
     * Check if player is hidden from the map
     *
     * @return True if player is hidden from the map
     */
    boolean isHidden();

    /**
     * Set if a player is hidden from the map
     *
     * @param hidden     True to hide, false to show
     * @param persistent True to persist this state
     */
    void setHidden(boolean hidden, boolean persistent);
}
