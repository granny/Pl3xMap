package net.pl3x.map.fabric.command;

import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class FabricSender extends Sender {
    public static @NonNull Sender create(@NonNull CommandSourceStack stack) {
        if (stack.source instanceof ServerPlayer) {
            return new Player(stack);
        }
        return new FabricSender(stack);
    }

    public FabricSender(@NonNull CommandSourceStack sender) {
        super(sender);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull CommandSourceStack getSender() {
        return super.getSender();
    }

    @Override
    public boolean hasPermission(@NonNull String permission) {
        return Permissions.check(getSender(), permission, Pl3xMap.api().getOperatorUserPermissionLevel());
    }

    @Override
    public void sendMessage(@NonNull String message) {
        getSender().sendMessage(Lang.parse(message));
    }

    @Override
    public void sendMessage(@NotNull String message, @NotNull TagResolver.@NonNull Single... placeholders) {
        getSender().sendMessage(Lang.parse(message, placeholders));
    }

    @Override
    public @NonNull String toString() {
        return "FabricSender{"
                + "sender=" + getSender().getTextName()
                + "}";
    }

    public static class Player extends FabricSender implements Audience, Sender.Player<ServerPlayer> {
        public Player(@NonNull CommandSourceStack sender) {
            super(sender);
        }

        @Override
        public @NonNull ServerPlayer getPlayer() {
            return (ServerPlayer) getSender().source;
        }

        @Override
        public @Nullable World getWorld() {
            return Pl3xMap.api().getWorldRegistry().get(getPlayer().getLevel().dimension().location().toString());
        }

        @Override
        public @NonNull String toString() {
            return "FabricPlayer{"
                    + "player=" + getPlayer().getScoreboardName()
                    + "}";
        }
    }
}
