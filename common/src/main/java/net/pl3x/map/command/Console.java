package net.pl3x.map.command;

import java.util.UUID;
import net.kyori.adventure.text.ComponentLike;
import net.pl3x.map.Key;
import net.pl3x.map.configuration.Lang;
import org.jetbrains.annotations.NotNull;

/**
 * Represents the console command sender.
 */
public abstract class Console extends Sender {
    public Console() {
        super(Key.of(new UUID(0, 0)));
    }

    @Override
    public void send(boolean prefix, @NotNull ComponentLike message) {
        sendMessage(Lang.parse("[<dark_aqua>Pl3xMap</dark_aqua>] ").append(message));
    }
}
