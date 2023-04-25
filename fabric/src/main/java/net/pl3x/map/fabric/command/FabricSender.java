package net.pl3x.map.fabric.command;

import java.util.Objects;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.fabric.FabricServerAudiences;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

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
    public @NonNull Audience audience() {
        return ((FabricServerAudiences) Pl3xMap.api().adventure()).audience(getSender());
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
        FabricSender other = (FabricSender) o;
        return getSender().source == other.getSender().source;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender().source);
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
        public void sendMessage(@NonNull String message) {
            sendMessage(Pl3xMap.api().adventure().player(getPlayer().getUUID()), true, Lang.parse(message));
        }

        @Override
        public void sendMessage(@NonNull String message, @NonNull TagResolver.@NonNull Single... placeholders) {
            sendMessage(Pl3xMap.api().adventure().player(getPlayer().getUUID()), true, Lang.parse(message, placeholders));
        }

        @Override
        public @NonNull String toString() {
            return "FabricSender$Player{"
                    + "player=" + getPlayer().getUUID()
                    + "}";
        }
    }
}
