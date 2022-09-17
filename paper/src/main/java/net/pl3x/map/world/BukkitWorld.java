package net.pl3x.map.world;

import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.player.Player;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitWorld extends World {
    public BukkitWorld(org.bukkit.World world) {
        super(createKey(world.getName()), ((CraftWorld) world).getHandle());
    }

    @Override
    @NotNull
    public Collection<Player> getPlayers() {
        return getLevel().players().stream()
                .map(player -> Pl3xMap.api().getPlayerRegistry().get(player.getUUID()))
                .collect(Collectors.toSet());
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
