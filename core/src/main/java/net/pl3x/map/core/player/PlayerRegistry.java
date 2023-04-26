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

import java.util.Locale;
import java.util.UUID;
import java.util.function.Supplier;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.core.util.Preconditions;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Manages player specific data
 */
public class PlayerRegistry extends Registry<@NonNull Player> {
    public @NonNull Player getOrDefault(@NonNull UUID uuid, @NonNull Supplier<@NonNull Player> supplier) {
        Player player = get(uuid);
        if (player == null) {
            player = supplier.get();
            register(player.getUUID(), player);
        }
        return player;
    }

    public @NonNull Player register(@NonNull UUID uuid, @NonNull Player player) {
        Preconditions.checkNotNull(uuid, "UUID cannot be null");
        Preconditions.checkNotNull(player, "Player cannot be null");
        return super.register(uuid.toString(), player);
    }

    public @Nullable Player unregister(@NonNull UUID uuid) {
        return super.unregister(uuid.toString());
    }

    /**
     * Get the registered player by uuid.
     * <p>
     * Will return null if no player registered.
     *
     * @param uuid player uuid
     * @return registered player or null
     */
    public @Nullable Player get(@NonNull UUID uuid) {
        return super.get(uuid.toString());
    }

    /**
     * Get the registered player by name.
     * <p>
     * Will return null if no player registered.
     *
     * @param name player name
     * @return registered player or null
     */
    public @Nullable Player get(@NonNull String name) {
        String lowercaseName = name.toLowerCase(Locale.ROOT);
        for (Player player : values()) {
            if (player.getName().toLowerCase(Locale.ROOT).equals(lowercaseName)) {
                return player;
            }
        }
        return null;
    }
}
