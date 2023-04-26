package net.pl3x.map.core.command;

import java.util.UUID;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Represents a command sender.
 */
public abstract class Sender implements ForwardingAudience.Single {
    private final Object sender;

    public <@NonNull T> Sender(@NonNull T sender) {
        this.sender = sender;
    }

    @SuppressWarnings("unchecked")
    public <@NonNull T> @NonNull T getSender() {
        return (T) this.sender;
    }

    public abstract boolean hasPermission(@NonNull String permission);

    public void sendMessage(@NonNull String message) {
        sendMessage(Pl3xMap.api().adventure().console(), true, Lang.parse(message));
    }

    public void sendMessage(@NonNull String message, @NonNull TagResolver.@NonNull Single... placeholders) {
        sendMessage(Pl3xMap.api().adventure().console(), true, Lang.parse(message, placeholders));
    }

    public void sendMessage(Audience audience, boolean prefix, @NonNull ComponentLike message) {
        audience.sendMessage(prefix ? Lang.parse(Lang.PREFIX_COMMAND).append(message) : message);
    }

    @Override
    public abstract boolean equals(@Nullable Object o);

    @Override
    public abstract int hashCode();

    @Override
    public abstract @NonNull String toString();

    public interface Player<T> {
        @NonNull T getPlayer();

        @NonNull UUID getUUID();

        @Nullable World getWorld();
    }
}
