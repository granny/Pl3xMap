package net.pl3x.map.bukkit;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.authlib.properties.Property;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.world.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.persistence.PersistentDataType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BukkitPlayer extends Player {
    private final NamespacedKey hiddenKey = new NamespacedKey(Pl3xMapBukkit.getProvidingPlugin(Pl3xMapBukkit.class), "hidden");

    public BukkitPlayer(org.bukkit.entity.@NonNull Player player) {
        super(player);
    }

    @Override
    @SuppressWarnings("unchecked")
    public org.bukkit.entity.@NonNull Player getPlayer() {
        return super.getPlayer();
    }

    @Override
    public @NonNull String getName() {
        return getPlayer().getName();
    }

    @Override
    public @NonNull UUID getUUID() {
        return getPlayer().getUniqueId();
    }

    @Override
    public @NonNull World getWorld() {
        World world = Pl3xMap.api().getWorldRegistry().get(getPlayer().getWorld().getName());
        if (world == null) {
            throw new IllegalStateException("Player is in an unloaded world!");
        }
        return world;
    }

    @Override
    public @NonNull Point getPosition() {
        Location loc = getPlayer().getLocation();
        return Point.of(loc.getBlockX(), loc.getBlockZ());
    }

    @Override
    public float getYaw() {
        return getPlayer().getLocation().getYaw();
    }

    @Override
    public int getHealth() {
        return (int) Math.round(getPlayer().getHealth());
    }

    @Override
    public int getArmorPoints() {
        AttributeInstance attr = getPlayer().getAttribute(Attribute.GENERIC_ARMOR);
        return attr == null ? 0 : (int) Math.round(attr.getValue());
    }

    @Override
    public @Nullable URL getSkin() {
        try {
            ServerPlayer player = ((CraftPlayer) getPlayer()).getHandle();
            Property property = player.getGameProfile().getProperties().get("textures").stream().findFirst().orElse(null);
            if (property == null) {
                return null;
            }
            String json = new String(Base64.getDecoder().decode(property.getValue()), StandardCharsets.UTF_8);
            JsonElement jsonElement = JsonParser.parseString(json);
            if (!jsonElement.isJsonObject()) {
                return null;
            }
            JsonObject jsonObject = jsonElement.getAsJsonObject().getAsJsonObject("textures");
            if (jsonObject == null) {
                return null;
            }
            JsonObject skin = jsonObject.get("SKIN").getAsJsonObject();
            if (skin == null) {
                return null;
            }
            String url = skin.get("url").getAsString();
            if (url == null) {
                return null;
            }
            return new URI(url).toURL();
        } catch (Throwable e) {
            return null;
        }
    }

    @Override
    public boolean isInvisible() {
        return getPlayer().isInvisible();
    }

    @Override
    public boolean isNPC() {
        return getPlayer().hasMetadata("NPC");
    }

    @Override
    public boolean isSpectator() {
        return getPlayer().getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isPersistentlyHidden() {
        return getPlayer().getPersistentDataContainer().getOrDefault(hiddenKey, PersistentDataType.BYTE, (byte) 0) != 0;
    }

    @Override
    public void setPersistentlyHidden(boolean hidden) {
        getPlayer().getPersistentDataContainer().set(hiddenKey, PersistentDataType.BYTE, (byte) (hidden ? 1 : 0));
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
        return Objects.hash(getPlayer().getUniqueId());
    }

    @Override
    public @NonNull String toString() {
        return "BukkitPlayer{"
                + "player=" + getPlayer().getUniqueId()
                + "}";
    }
}
