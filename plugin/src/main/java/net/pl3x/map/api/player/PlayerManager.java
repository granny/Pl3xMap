package net.pl3x.map.api.player;

import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Manages player specific data
 */
public interface PlayerManager {
    MapPlayer getPlayer(UUID uuid);

    void unloadPlayer(UUID uuid);

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
    void registerNameDecorator(int priority, BiFunction<MapPlayer, String, String> decorator);

    String decorateName(MapPlayer player);
}
