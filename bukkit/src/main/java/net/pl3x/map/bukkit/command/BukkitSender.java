package net.pl3x.map.bukkit.command;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.bukkit.command.CommandSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class BukkitSender extends Sender {
    public static @NonNull Sender create(@NonNull CommandSender sender) {
        if (sender instanceof org.bukkit.entity.Player) {
            return new BukkitPlayer(sender);
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
    public void sendMessage(@NonNull String message) {
        Pl3xMap.api().adventure().console().sendMessage(Lang.parse(message));
    }

    @Override
    public void sendMessage(@NotNull String message, @NotNull TagResolver.@NonNull Single... placeholders) {
        Pl3xMap.api().adventure().console().sendMessage(Lang.parse(message, placeholders));
    }

    @Override
    public @NonNull String toString() {
        return "BukkitSender{"
                + "sender=" + getSender().getName()
                + "}";
    }

    public static class BukkitPlayer extends BukkitSender implements Audience, Sender.Player<org.bukkit.entity.Player> {
        public BukkitPlayer(@NonNull CommandSender sender) {
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
            Pl3xMap.api().adventure().player(getPlayer().getUniqueId()).sendMessage(Lang.parse(message));
        }

        @Override
        public void sendMessage(@NotNull String message, @NotNull TagResolver.@NonNull Single... placeholders) {
            Pl3xMap.api().adventure().player(getPlayer().getUniqueId()).sendMessage(Lang.parse(message, placeholders));
        }

        @Override
        public @NonNull String toString() {
            return "BukkitPlayer{"
                    + "player=" + getSender().getName()
                    + "}";
        }
    }
}
