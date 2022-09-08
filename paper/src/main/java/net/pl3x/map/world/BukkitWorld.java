package net.pl3x.map.world;

import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

public class BukkitWorld extends World {
    public BukkitWorld(org.bukkit.World world) {
        super(createKey(world.getName()), ((CraftWorld) world).getHandle());
    }
}
