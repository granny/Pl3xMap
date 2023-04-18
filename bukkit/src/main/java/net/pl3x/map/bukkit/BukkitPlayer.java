package net.pl3x.map.bukkit;

import java.net.URL;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.world.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BukkitPlayer extends Player {
    private static final NamespacedKey HIDDEN_KEY = new NamespacedKey(Pl3xMapBukkit.getInstance(), "hidden");

    private final org.bukkit.entity.Player player;

    public BukkitPlayer(org.bukkit.entity.Player player) {
        this.player = player;
    }

    public org.bukkit.entity.Player getPlayer() {
        return this.player;
    }

    @Override
    @NonNull
    public String getName() {
        return this.player.getName();
    }

    @Override
    @NonNull
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    @Override
    @NonNull
    public World getWorld() {
        World world = Pl3xMap.api().getWorldRegistry().get(this.player.getWorld().getName());
        if (world == null) {
            throw new IllegalStateException("Player is in an unloaded world!");
        }
        return world;
    }

    @Override
    @NonNull
    public Point getPosition() {
        Location loc = this.player.getLocation();
        return Point.of(loc.getBlockX(), loc.getBlockZ());
    }

    @Override
    public float getYaw() {
        return this.player.getLocation().getYaw();
    }

    @Override
    public int getHealth() {
        return (int) Math.round(this.player.getHealth());
    }

    @Override
    public int getArmorPoints() {
        AttributeInstance attr = this.player.getAttribute(Attribute.GENERIC_ARMOR);
        return attr == null ? 0 : (int) Math.round(attr.getValue());
    }

    @Override
    @Nullable
    public URL getSkin() {
        return this.player.getPlayerProfile().getTextures().getSkin();
    }

    @Override
    public boolean isInvisible() {
        return this.player.isInvisible();
    }

    @Override
    public boolean isNPC() {
        return this.player.hasMetadata("NPC");
    }

    @Override
    public boolean isSpectator() {
        return this.player.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isPersistentlyHidden() {
        return this.player.getPersistentDataContainer().getOrDefault(HIDDEN_KEY, PersistentDataType.BYTE, (byte) 0) != 0;
    }

    @Override
    public void setPersistentlyHidden(boolean hidden) {
        this.player.getPersistentDataContainer().set(HIDDEN_KEY, PersistentDataType.BYTE, (byte) (hidden ? 1 : 0));
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
        BukkitPlayer other = (BukkitPlayer) o;
        return getUUID().equals(other.getUUID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.player.getUniqueId());
    }

    @Override
    @NonNull
    public String toString() {
        return "BukkitPlayer{"
                + "player=" + getPlayer().getUniqueId()
                + "}";
    }
}
