package net.pl3x.map.player;

import java.util.Collections;
import java.util.Map;
import net.pl3x.map.Key;
import net.pl3x.map.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages player specific data
 */
public abstract class PlayerRegistry extends Registry<Player> {
    @NotNull
    public Player register(@NotNull Player player) {
        return register(player.getKey(), player);
    }

    @Override
    @NotNull
    public Player register(@NotNull Key key, @NotNull Player player) {
        if (this.entries.containsKey(key)) {
            throw new IllegalArgumentException("Player is already loaded");
        }
        this.entries.put(player.getKey(), player);
        return player;
    }

    @Nullable
    public Player unregister(@NotNull String name) {
        return unregister(Player.createKey(name));
    }

    @Override
    @Nullable
    public Player unregister(@NotNull Key key) {
        return this.entries.remove(key);
    }

    @Override
    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    @Nullable
    public Player get(String name) {
        return get(Player.createKey(name));
    }

    @Override
    @Nullable
    public Player get(@NotNull Key key) {
        return this.entries.get(key);
    }

    @Override
    @NotNull
    public Map<Key, Player> entries() {
        return Collections.unmodifiableMap(this.entries);
    }
}
