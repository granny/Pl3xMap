package net.pl3x.map.core.player;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Player event listener.
 */
public interface PlayerListener {
    /**
     * Fired when a player joins the server.
     *
     * @param player player that joined
     */
    default void onJoin(@Nullable Player player) {
        if (player == null) {
            return;
        }
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
    default void onQuit(@Nullable Player player) {
    }
}
