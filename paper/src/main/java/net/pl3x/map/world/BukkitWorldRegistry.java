package net.pl3x.map.world;

import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitWorldRegistry extends WorldRegistry {
    @Nullable
    public MapWorld register(@NotNull org.bukkit.World bukkit) {
        return register(new BukkitWorld(bukkit));
    }

    @Nullable
    public MapWorld unregister(org.bukkit.World world) {
        return unregister(((CraftWorld) world).getHandle());
    }

    @Nullable
    public MapWorld get(org.bukkit.World world) {
        return get(((CraftWorld) world).getHandle());
    }
}
