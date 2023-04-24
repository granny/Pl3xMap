package net.pl3x.map.forge.command;

import com.google.gson.JsonElement;
import java.util.Objects;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerPlayer;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

public class ForgeSender extends Sender {
    public static @NonNull Sender create(@NonNull CommandSourceStack stack) {
        if (stack.source instanceof ServerPlayer) {
            return new Player(stack);
        }
        return new ForgeSender(stack);
    }

    public ForgeSender(@NonNull CommandSourceStack sender) {
        super(sender);
    }

    @Override
    @SuppressWarnings("unchecked")
    public @NonNull CommandSourceStack getSender() {
        return super.getSender();
    }

    @Override
    public boolean hasPermission(@NonNull String permission) {
        return true; // getSender().hasPermission(permission);
    }

    @Override
    public void sendMessage(@NonNull String message) {
        getSender().sendSystemMessage(toNative(Lang.parse(message)));
    }

    @Override
    public void sendMessage(@NotNull String message, @NotNull TagResolver.@NonNull Single... placeholders) {
        getSender().sendSystemMessage(toNative(Lang.parse(message, placeholders)));
    }

    private static net.minecraft.network.chat.Component toNative(Component component) {
        final JsonElement tree = GsonComponentSerializer.gson().serializeToTree(component);
        return Objects.requireNonNull(net.minecraft.network.chat.Component.Serializer.fromJson(tree));
    }

    public static class Player extends ForgeSender implements Audience, Sender.Player<ServerPlayer> {
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
    }
}
