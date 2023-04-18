package net.pl3x.map.fabric;

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
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FabricPlayer extends Player {
    //private static final NamespacedKey HIDDEN_KEY = new NamespacedKey(Pl3xMapBukkit.getInstance(), "hidden");

    private final net.minecraft.world.entity.player.Player player;

    public FabricPlayer(net.minecraft.world.entity.player.Player player) {
        this.player = player;
    }

    public net.minecraft.world.entity.player.Player getPlayer() {
        return this.player;
    }

    @Override
    @NonNull
    public String getName() {
        return this.player.getScoreboardName();
    }

    @Override
    @NonNull
    public UUID getUUID() {
        return this.player.getUUID();
    }

    @Override
    @NonNull
    public World getWorld() {
        World world = Pl3xMap.api().getWorldRegistry().get(this.player.getLevel().dimension().location().toString());
        if (world == null) {
            throw new IllegalStateException("Player is in an unloaded world!");
        }
        return world;
    }

    @Override
    @NonNull
    public Point getPosition() {
        Vec3 loc = this.player.position();
        return Point.of(loc.x(), loc.z());
    }

    @Override
    public float getYaw() {
        return this.player.getYRot();
    }

    @Override
    public int getHealth() {
        return Math.round(this.player.getHealth());
    }

    @Override
    public int getArmorPoints() {
        AttributeInstance attr = this.player.getAttribute(Attributes.ARMOR);
        return attr == null ? 0 : (int) Math.round(attr.getValue());
    }

    @Override
    @Nullable
    public URL getSkin() {
        try {
            Property property = this.player.getGameProfile().getProperties().get("textures").stream().findFirst().orElse(null);
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
        return this.player.isInvisible();
    }

    @Override
    public boolean isNPC() {
        return false;
    }

    @Override
    public boolean isSpectator() {
        return this.player.isSpectator();
    }

    @Override
    public boolean isPersistentlyHidden() {
        return false;//this.player.getPersistentDataContainer().getOrDefault(HIDDEN_KEY, PersistentDataType.BYTE, (byte) 0) != 0;
    }

    @Override
    public void setPersistentlyHidden(boolean hidden) {
        //this.player.getPersistentDataContainer().set(HIDDEN_KEY, PersistentDataType.BYTE, (byte) (hidden ? 1 : 0));
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return this == o;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.player.getUUID());
    }

    @Override
    @NonNull
    public String toString() {
        return "BukkitPlayer{"
                + "player=" + getPlayer().getUUID()
                + "}";
    }
}
