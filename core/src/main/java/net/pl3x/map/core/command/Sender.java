package net.pl3x.map.core.command;

import java.util.Objects;
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

    @Override
    public @NonNull Audience audience() {
        return getSender();
    }

    public abstract boolean hasPermission(@NonNull String permission);

    public abstract void sendMessage(@NonNull String message);

    public abstract void sendMessage(@NotNull String message, @NotNull TagResolver.@NonNull Single... placeholders);

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
        Sender other = (Sender) o;
        return getSender() == other.getSender();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getSender());
    }

    public interface Player<T> {
        @NonNull T getPlayer();

        @Nullable World getWorld();
    }
}
