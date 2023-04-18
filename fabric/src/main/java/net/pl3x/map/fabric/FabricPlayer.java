package net.pl3x.map.fabric;

import com.google.gson.Gson;
import com.mojang.authlib.properties.Property;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.Vec3;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.player.PlayerTexture;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class FabricPlayer extends Player {
    //private static final NamespacedKey HIDDEN_KEY = new NamespacedKey(Pl3xMapBukkit.getInstance(), "hidden");

    private final net.minecraft.world.entity.player.Player player;

    private Map<BiFunction<Player, String, String>, Integer> nameDecorators = new LinkedHashMap<>();

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
        PlayerTexture.Json parsed = null;
        Collection<Property> textures = this.player.getGameProfile().getProperties().get("textures");
        if (textures.isEmpty()) {
            return null;
        }
        for (Property texture : textures) {
            Gson gson = new Gson();
            String value = texture.getValue();
            byte[] decoded = Base64.getDecoder().decode(value);
            String json = new String(decoded, StandardCharsets.UTF_8);
            parsed = gson.fromJson(json, PlayerTexture.Json.class);
            break;
        }
        if (parsed == null) {
            return null;
        }
        Map<String, String> skin = parsed.textures.get("SKIN");
        if (skin == null) {
            return null;
        }
        String url = skin.get("url");
        if (url == null) {
            return null;
        }
        try {
            System.out.println("Texture URL: " + url);
            return new URI(url).toURL();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new RuntimeException(e);
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
    public void registerNameDecorator(int priority, @NonNull BiFunction<@NonNull Player, @NonNull String, @NonNull String> decorator) {
        this.nameDecorators.put(decorator, priority);

        this.nameDecorators = this.nameDecorators.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    @NonNull
    public String getDecoratedName() {
        String name = getName();
        for (BiFunction<Player, String, String> fn : this.nameDecorators.keySet()) {
            name = fn.apply(this, name);
        }
        return name;
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
