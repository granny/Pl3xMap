package net.pl3x.map.bukkit.command;

import java.util.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class BukkitSender extends Sender {
    public static @NonNull Sender create(@NonNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            return new Player(sender);
        }
        return new BukkitSender(sender);
    }

    public BukkitSender(@NonNull CommandSender sender) {
        super(sender);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull CommandSender getSender() {
        return super.getSender();
    }

    @Override
    public boolean hasPermission(@NonNull String permission) {
        return getSender().hasPermission(permission);
    }

    @Override
    public @NonNull Audience audience() {
        return ((BukkitAudiences) Pl3xMap.api().adventure()).sender(getSender());
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
        BukkitSender other = (BukkitSender) o;
        return getSender() == other.getSender();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender());
    }

    @Override
    public @NonNull String toString() {
        return "BukkitSender{"
                + "sender=" + getSender().getName()
                + "}";
    }

    public static class Player extends BukkitSender implements Audience, Sender.Player<org.bukkit.entity.Player> {
        public Player(@NonNull CommandSender sender) {
            super(sender);
        }

        @Override
        public org.bukkit.entity.@NonNull Player getPlayer() {
            return (org.bukkit.entity.Player) getSender();
        }

        @Override
        public @Nullable World getWorld() {
            return Pl3xMap.api().getWorldRegistry().get(getPlayer().getWorld().getName());
        }

        @Override
        public void sendMessage(@NonNull String message) {
            sendMessage(Pl3xMap.api().adventure().player(getPlayer().getUniqueId()), true, Lang.parse(message));
        }

        @Override
        public void sendMessage(@NonNull String message, @NonNull TagResolver.@NonNull Single... placeholders) {
            sendMessage(Pl3xMap.api().adventure().player(getPlayer().getUniqueId()), true, Lang.parse(message, placeholders));
        }

        @Override
        public @NonNull String toString() {
            return "BukkitSender$Player{"
                    + "player=" + getPlayer().getUniqueId()
                    + "}";
        }
    }
}
