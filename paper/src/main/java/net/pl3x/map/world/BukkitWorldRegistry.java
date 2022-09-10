package net.pl3x.map.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitWorldRegistry extends WorldRegistry {
    /**
     * Register a new world by bukkit world.
     * <p>
     * Will return null if world is already registered.
     *
     * @param world bukkit world to register
     * @return registered world or null
     */
    @Nullable
    public World register(@NotNull org.bukkit.World world) {
        return register(new BukkitWorld(world));
    }

    /**
     * Unregister the specified world.
     * <p>
     * Will return null if world is not registered.
     *
     * @param world bukkit world to unregister
     * @return unregistered world or null
     */
    @Nullable
    public World unregister(@NotNull org.bukkit.World world) {
        return unregister(world.getName());
    }

    /**
     * Get the registered world by bukkit world.
     * <p>
     * Will return null if no world registered.
     *
     * @param world bukkit world
     * @return registered world or null
     */
    @Nullable
    public World get(@NotNull org.bukkit.World world) {
        return get(world.getName());
    }
}
