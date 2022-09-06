package net.pl3x.map.world;

import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;

public class BukkitWorld extends World {
    private final String name;

    public BukkitWorld(org.bukkit.World world) {
        super(((CraftWorld) world).getHandle());
        this.name = world.getName();
    }

    @Override
    @NotNull
    public String getName() {
        return this.name;
    }
}
