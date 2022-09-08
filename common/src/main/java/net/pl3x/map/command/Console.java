package net.pl3x.map.command;

import net.kyori.adventure.text.ComponentLike;
import net.pl3x.map.Key;
import net.pl3x.map.configuration.Lang;
import org.jetbrains.annotations.NotNull;

public abstract class Console extends Sender {
    public Console() {
        super(new Key("server:console"));
    }

    @Override
    public void send(boolean prefix, @NotNull ComponentLike component) {
        sendMessage(prefix ? Lang.parse(Lang.PREFIX_LOGGER).append(component) : component);
    }
}
