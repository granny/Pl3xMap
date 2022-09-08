package net.pl3x.map.command;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.configuration.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class Sender extends Keyed implements Audience {
    public Sender(@NotNull Key key) {
        super(key);
    }

    public void send(@Nullable String msg, @NotNull TagResolver.Single... placeholders) {
        send(true, msg, placeholders);
    }

    public void send(boolean prefix, @Nullable String msg, @NotNull TagResolver.Single... placeholders) {
        if (msg == null) {
            return;
        }
        for (String part : msg.split("\n")) {
            send(prefix, Lang.parse(part, placeholders));
        }
    }

    public void send(@NotNull ComponentLike component) {
        send(true, component);
    }

    public abstract void send(boolean prefix, @NotNull ComponentLike component);
}
