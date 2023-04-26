package net.pl3x.map.core.command.exception;

import net.pl3x.map.core.command.Sender.Player;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown to indicate that a method has been passed an illegal or inappropriate {@link Player} argument.
 */
public class PlayerParseException extends ArgumentParseException {
    public static final Reason MUST_SPECIFY_PLAYER = new Reason(() -> Lang.ERROR_MUST_SPECIFY_PLAYER);
    public static final Reason NO_SUCH_PLAYER = new Reason(() -> Lang.ERROR_NO_SUCH_PLAYER);

    /**
     * Construct a new PlayerParseException.
     *
     * @param input  Input
     * @param reason Failure reason
     */
    public PlayerParseException(@Nullable String input, @NonNull Reason reason) {
        super(input, "<player>", reason);
    }
}
