package net.pl3x.map.player;

import java.net.URL;
import java.util.UUID;
import java.util.function.BiFunction;
import net.kyori.adventure.text.ComponentLike;
import net.pl3x.map.Key;
import net.pl3x.map.command.Sender;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.markers.Point;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a player.
 */
public abstract class Player extends Sender {
    private boolean hidden;

    public Player(Key key) {
        super(key);
    }

    /**
     * Create a new key.
     *
     * @param name player name
     * @return a new key
     */
    public static Key createKey(@NotNull String name) {
        return Key.of("player:" + name);
    }

    /**
     * Get the player's name.
     *
     * @return player's name
     */
    @NotNull
    public abstract String getName();

    /**
     * Get the player's UUID.
     *
     * @return player's UUID
     */
    @NotNull
    public abstract UUID getUUID();

    /**
     * Get the world this player is currently in.
     *
     * @return player's world
     */
    @NotNull
    public abstract World getWorld();

    /**
     * Get the player's current position.
     *
     * @return player's position
     */
    @NotNull
    public abstract Point getPosition();

    /**
     * Get the player's current yaw.
     *
     * @return player's yaw
     */
    public abstract float getYaw();

    /**
     * Get the player's current health
     *
     * @return player's health
     */
    public abstract int getHealth();

    /**
     * Get the player's current armor points.
     *
     * @return player's armor points
     */
    public abstract int getArmorPoints();

    /**
     * Get the player's skin URL.
     *
     * @return player's skin URL
     */
    @Nullable
    public abstract URL getSkin();

    /**
     * Get whether the player is invisible.
     *
     * @return true if player is invisible
     */
    public abstract boolean isInvisible();

    /**
     * Get whether the player is an NPC.
     * <p>
     * This is for things like the Citizens plugin.
     *
     * @return true if player is an NPC
     */
    public abstract boolean isNPC();

    /**
     * Get whether the player is in spectator gamemode.
     *
     * @return true if player is spectator
     */
    public abstract boolean isSpectator();

    /**
     * Get whether player is hidden from the map.
     * <p>
     * This method checks the tracker settings.
     *
     * @return true if player is hidden
     */
    public boolean isHidden() {
        if (this.hidden) {
            return true;
        }
        if (getWorld().getConfig().PLAYER_TRACKER_HIDE_SPECTATORS && isSpectator()) {
            return true;
        }
        if (getWorld().getConfig().PLAYER_TRACKER_HIDE_INVISIBLE && isInvisible()) {
            return true;
        }
        return isPersistentlyHidden();
    }

    /**
     * Set if the player is hidden from the map
     *
     * @param hidden     True to hide, false to show
     * @param persistent True to persist this state
     */
    public void setHidden(boolean hidden, boolean persistent) {
        this.hidden = hidden;
        if (persistent) {
            setPersistentlyHidden(hidden);
        }
    }

    /**
     * Get whether the player has hidden flag set persistently.
     *
     * @return true if player is persistently hidden
     */
    public abstract boolean isPersistentlyHidden();

    /**
     * Set whether the player has hidden flag set persistently.
     *
     * @param hidden true to persistently hide player
     */
    public abstract void setPersistentlyHidden(boolean hidden);

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
    public abstract void registerNameDecorator(int priority, @NotNull BiFunction<@NotNull Player, @NotNull String, @NotNull String> decorator);

    /**
     * Get the player's decorated name.
     *
     * @return decorated name
     */
    @NotNull
    public abstract String getDecoratedName();

    @Override
    public void send(boolean prefix, @NotNull ComponentLike message) {
        sendMessage(prefix ? Lang.parse(Lang.PREFIX_COMMAND).append(message) : message);
    }
}
