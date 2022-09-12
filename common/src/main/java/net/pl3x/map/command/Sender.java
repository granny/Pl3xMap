package net.pl3x.map.command;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.configuration.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a command sender.
 */
public abstract class Sender extends Keyed implements Audience {
    public Sender(@NotNull Key key) {
        super(key);
    }

    /**
     * Send a message.
     *
     * @param message      message to send
     * @param placeholders message placeholders
     */
    public void send(@Nullable String message, @NotNull TagResolver.Single... placeholders) {
        send(true, message, placeholders);
    }

    /**
     * Send a message.
     *
     * @param prefix       true to insert command prefix
     * @param message      message to send
     * @param placeholders message placeholders
     */
    public void send(boolean prefix, @Nullable String message, @NotNull TagResolver.Single... placeholders) {
        if (message == null) {
            return;
        }
        for (String part : message.split("\n")) {
            send(prefix, Lang.parse(part, placeholders));
        }
    }

    /**
     * Send a message.
     *
     * @param message message to send
     */
    public void send(@NotNull ComponentLike message) {
        send(true, message);
    }

    /**
     * Send a message.
     *
     * @param prefix  true to insert command prefix
     * @param message message to send
     */
    public abstract void send(boolean prefix, @NotNull ComponentLike message);
}
