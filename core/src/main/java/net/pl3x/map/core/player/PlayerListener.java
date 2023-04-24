package net.pl3x.map.core.player;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Player event listener.
 */
public class PlayerListener {
    /**
     * Fired when a player joins the server.
     *
     * @param player player that joined
     */
    public void onJoin(@NonNull Player player) {
        if (player.isHidden()) {
            player.setHidden(true, false);
        }
        new PlayerTexture(player).start();
    }

    /**
     * Fired when a player leaves the server.
     *
     * @param player player that left
     */
    public void onQuit(@NonNull Player player) {
    }
}
