package net.pl3x.map.world;

import java.util.Collections;
import java.util.Map;
import net.minecraft.server.level.ServerLevel;
import net.pl3x.map.Key;
import net.pl3x.map.event.world.WorldUnloadedEvent;
import net.pl3x.map.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages loaded worlds
 */
public abstract class WorldRegistry extends Registry<World> {
    @Nullable
    public World register(@NotNull World world) {
        return register(world.getKey(), world);
    }

    @Override
    @Nullable
    public World register(@NotNull Key key, @NotNull World world) {
        if (this.entries.containsKey(key)) {
            throw new IllegalArgumentException("World is already loaded");
        }
        if (!key.equals(world.getKey())) {
            throw new IllegalArgumentException("Key does not match world");
        }
        world.init();
        this.entries.put(world.getKey(), world);
        return world;
    }

    @Nullable
    public World unregister(@NotNull ServerLevel level) {
        return unregister(World.createKey(level));
    }

    @Nullable
    public World unregister(@NotNull String name) {
        return unregister(World.createKey(name));
    }

    @Override
    @Nullable
    public World unregister(@NotNull Key key) {
        World world = this.entries.remove(key);
        if (world != null) {
            world.unload();
            new WorldUnloadedEvent(world).callEvent();
        }
        return world;
    }

    public void unregister() {
        Collections.unmodifiableSet(this.entries.keySet()).forEach(this::unregister);
    }

    @Nullable
    public World get(@NotNull ServerLevel level) {
        return get(World.createKey(level));
    }

    @Nullable
    public World get(String name) {
        return get(World.createKey(name));
    }

    @Override
    @Nullable
    public World get(@NotNull Key key) {
        return this.entries.get(key);
    }

    @Override
    @NotNull
    public Map<Key, World> entries() {
        return Collections.unmodifiableMap(this.entries);
    }
}
