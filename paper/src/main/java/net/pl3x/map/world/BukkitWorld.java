package net.pl3x.map.world;

import java.util.Objects;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.jetbrains.annotations.Nullable;

public class BukkitWorld extends World {
    public BukkitWorld(org.bukkit.World world) {
        super(createKey(world.getName()), ((CraftWorld) world).getHandle());
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        BukkitWorld other = (BukkitWorld) o;
        return getKey() == other.getKey()
                && getLevel() == other.getLevel();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getLevel());
    }

    @Override
    public String toString() {
        return "BukkitWorld{"
                + "key=" + getKey()
                + ",world=" + getLevel().getWorld().getName()
                + "}";
    }
}
