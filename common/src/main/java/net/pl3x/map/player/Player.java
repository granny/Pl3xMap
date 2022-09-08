package net.pl3x.map.player;

import java.util.UUID;
import java.util.function.BiFunction;
import net.kyori.adventure.text.ComponentLike;
import net.pl3x.map.Key;
import net.pl3x.map.command.Sender;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.markers.Point;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public abstract class Player extends Sender {
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

    @NotNull
    public abstract String getName();

    @NotNull
    public abstract UUID getUUID();

    @NotNull
    public abstract World getWorld();

    @NotNull
    public abstract Point getPosition();

    public abstract float getYaw();

    public abstract int getHealth();

    public abstract int getArmorPoints();
    // AttributeInstance attr = player.getAttribute(Attributes.ARMOR);
    // return attr == null ? 0 : (int) attr.getValue();

    public abstract boolean isInvisible();

    public abstract boolean isNPC();

    public abstract boolean isSpectator();

    /**
     * Check if player is hidden from the map
     *
     * @return True if player is hidden from the map
     */
    public abstract boolean isHidden();

    /**
     * Set if a player is hidden from the map
     *
     * @param hidden     True to hide, false to show
     * @param persistent True to persist this state
     */
    public abstract void setHidden(boolean hidden, boolean persistent);

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

    @NotNull
    public abstract String getDecoratedName();

    @Override
    public void send(boolean prefix, @NotNull ComponentLike component) {
        sendMessage(prefix ? Lang.parse(Lang.PREFIX_COMMAND).append(component) : component);
    }
}
