package net.pl3x.map.world;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitWorldRegistry extends WorldRegistry {
    @Nullable
    public World register(@NotNull org.bukkit.World world) {
        return register(new BukkitWorld(world));
    }

    @Nullable
    public World unregister(@NotNull org.bukkit.World world) {
        return unregister(world.getName());
    }

    @Nullable
    public World get(@NotNull org.bukkit.World world) {
        return get(world.getName());
    }
}
