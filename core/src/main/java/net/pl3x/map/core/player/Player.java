/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.player;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a player.
 */
public abstract class Player {
    private final Object player;

    private Map<@NonNull BiFunction<@NonNull Player, @NonNull String, @NonNull String>, @NonNull Integer> nameDecorators = new LinkedHashMap<>();
    private boolean hidden;

    public <@NonNull T> Player(@NonNull T player) {
        this.player = player;
    }

    @SuppressWarnings("unchecked")
    public <@NonNull T> @NonNull T getPlayer() {
        return (T) this.player;
    }

    /**
     * Get the player's name.
     *
     * @return player's name
     */
    public abstract @NonNull String getName();

    /**
     * Get the player's UUID.
     *
     * @return player's UUID
     */
    public abstract @NonNull UUID getUUID();

    /**
     * Get the world this player is currently in.
     *
     * @return player's world
     */
    public abstract @NonNull World getWorld();

    /**
     * Get the player's current position.
     *
     * @return player's position
     */
    public abstract @NonNull Point getPosition();

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
    public abstract @Nullable URL getSkin();

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
     *
     * @return true if player is hidden
     */
    public boolean isHidden() {
        if (this.hidden) {
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
    public void registerNameDecorator(int priority, @NonNull BiFunction<@NonNull Player, @NonNull String, @NonNull String> decorator) {
        this.nameDecorators.put(decorator, priority);
        this.nameDecorators = this.nameDecorators.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    /**
     * Get the player's decorated name.
     *
     * @return decorated name
     */
    public @NonNull String getDecoratedName() {
        String name = getName();
        for (BiFunction<Player, String, String> fn : this.nameDecorators.keySet()) {
            name = fn.apply(this, name);
        }
        return name;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        Player other = (Player) o;
        return getUUID().equals(other.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUUID());
    }
}
