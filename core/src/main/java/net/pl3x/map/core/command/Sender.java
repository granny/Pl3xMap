package net.pl3x.map.core.command;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.NotNull;

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

    public @NonNull Audience audience() {
        return getSender();
    }

    public abstract boolean hasPermission(@NonNull String permission);

    public abstract void sendMessage(@NonNull String message);

    public abstract void sendMessage(@NotNull String message, @NotNull TagResolver.@NonNull Single... placeholders);

    public interface Player<T> {
        @NonNull T getPlayer();

        @Nullable World getWorld();
    }
}
