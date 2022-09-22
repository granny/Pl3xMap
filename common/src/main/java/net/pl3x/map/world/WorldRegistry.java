package net.pl3x.map.world;

import net.pl3x.map.Key;
import net.pl3x.map.event.world.WorldUnloadedEvent;
import net.pl3x.map.registry.KeyedRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages loaded worlds
 */
public abstract class WorldRegistry extends KeyedRegistry<World> {
    /**
     * Register a new world.
     * <p>
     * Will return null if the world is already registered.
     *
     * @param world world to register
     * @return registered world or null
     */
    public World register(@Nullable World world) {
        World result = super.register(world);
        if (result != null) {
            result.init();
        }
        return result;
    }

    /**
     * Unregister the specified world by name.
     * <p>
     * Will return null if world is not registered.
     *
     * @param name server name to unregister
     * @return unregistered world or null
     */
    @Nullable
    public World unregister(@NotNull String name) {
        return unregister(World.createKey(name));
    }

    /**
     * Unregister the world for the provided key.
     * <p>
     * Will return null if no world registered with provided key.
     *
     * @param key key
     * @return unregistered world or null
     */
    @Override
    @Nullable
    public World unregister(@NotNull Key key) {
        World world = super.unregister(key);
        if (world != null) {
            world.unload();
            new WorldUnloadedEvent(world).callEvent();
        }
        return world;
    }

    /**
     * Get the registered world by name.
     * <p>
     * Will return null if no world registered.
     *
     * @param name world name
     * @return registered world or null
     */
    @Nullable
    public World get(String name) {
        return get(World.createKey(name));
    }
}
