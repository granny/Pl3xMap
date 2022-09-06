package net.pl3x.map.world;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerLevel;
import net.pl3x.map.Key;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.event.world.WorldUnloadedEvent;
import net.pl3x.map.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages loaded worlds
 */
public class WorldRegistry implements Registry<MapWorld> {
    private final Map<Key, MapWorld> mapWorlds = new ConcurrentHashMap<>();

    @Nullable
    public MapWorld register(@NotNull World world, @NotNull WorldConfig config) {
        MapWorld mapWorld = new MapWorld(world, config);
        return register(world.getKey(), mapWorld);
    }

    @Override
    @Nullable
    public MapWorld register(@NotNull Key key, @NotNull MapWorld mapWorld) {
        if (this.mapWorlds.containsKey(key)) {
            throw new IllegalArgumentException("World is already loaded");
        }
        if (!key.equals(mapWorld.getWorld().getKey())) {
            throw new IllegalArgumentException("Key does not match world");
        }
        mapWorld.init();
        this.mapWorlds.put(mapWorld.getWorld().getKey(), mapWorld);
        return mapWorld;
    }

    @Nullable
    public MapWorld unregister(@NotNull ServerLevel level) {
        return unregister(Key.of(level));
    }

    @Override
    @Nullable
    public MapWorld unregister(@NotNull Key key) {
        MapWorld mapWorld = this.mapWorlds.remove(key);
        if (mapWorld != null) {
            mapWorld.unload();
            new WorldUnloadedEvent(mapWorld).callEvent();
        }
        return mapWorld;
    }

    public void unregister() {
        Collections.unmodifiableSet(this.mapWorlds.keySet()).forEach(this::unregister);
    }

    @Override
    @Nullable
    public MapWorld get(@NotNull Key key) {
        return this.mapWorlds.get(key);
    }

    @Nullable
    public MapWorld get(@NotNull ServerLevel level) {
        return get(Key.of(level));
    }

    @Override
    @NotNull
    public Map<Key, MapWorld> entries() {
        return Collections.unmodifiableMap(this.mapWorlds);
    }
}
