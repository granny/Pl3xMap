package net.pl3x.map.player;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import net.kyori.adventure.audience.MessageType;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.identity.Identity;
import net.kyori.adventure.text.Component;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.markers.Point;
import net.pl3x.map.world.World;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BukkitPlayer extends Player {
    private static final NamespacedKey HIDDEN_KEY = new NamespacedKey(PaperPl3xMap.getInstance(), "hidden");

    private final org.bukkit.entity.Player player;

    private Map<BiFunction<Player, String, String>, Integer> nameDecorators = new LinkedHashMap<>();

    public BukkitPlayer(org.bukkit.entity.Player player) {
        super(createKey(player.getName()));
        this.player = player;
    }

    public org.bukkit.entity.Player getPlayer() {
        return this.player;
    }

    @Override
    @NotNull
    public String getName() {
        return this.player.getName();
    }

    @Override
    @NotNull
    public UUID getUUID() {
        return this.player.getUniqueId();
    }

    @Override
    @NotNull
    public World getWorld() {
        World world = PaperPl3xMap.getInstance().getWorldRegistry().get(this.player.getWorld());
        if (world == null) {
            throw new IllegalStateException("Player is in an unloaded world!");
        }
        return world;
    }

    @Override
    @NotNull
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
        return (int) this.player.getHealth();
    }

    @Override
    public int getArmorPoints() {
        AttributeInstance attr = this.player.getAttribute(Attribute.GENERIC_ARMOR);
        return attr == null ? 0 : (int) attr.getValue();
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
    public void registerNameDecorator(int priority, @NotNull BiFunction<@NotNull Player, @NotNull String, @NotNull String> decorator) {
        this.nameDecorators.put(decorator, priority);

        this.nameDecorators = this.nameDecorators.entrySet().stream()
                .sorted((k1, k2) -> -k1.getValue().compareTo(k2.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }

    @Override
    @NotNull
    public String getDecoratedName() {
        String name = getName();
        for (BiFunction<Player, String, String> fn : this.nameDecorators.keySet()) {
            name = fn.apply(this, name);
        }
        return name;
    }

    // Rest of this implements Audience methods

    @Override
    public void sendMessage(@NotNull Identity source, @NotNull Component message, @NotNull MessageType type) {
        this.player.sendMessage(source, message, type);
    }

    @Override
    public void showBossBar(@NotNull BossBar bar) {
        this.player.showBossBar(bar);
    }

    @Override
    public void hideBossBar(@NotNull BossBar bar) {
        this.player.hideBossBar(bar);
    }
}
