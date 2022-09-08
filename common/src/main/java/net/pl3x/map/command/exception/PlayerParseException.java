package net.pl3x.map.command.exception;

import net.pl3x.map.configuration.Lang;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlayerParseException extends ArgumentParseException {
    public static final Reason MUST_SPECIFY_PLAYER = new Reason(() -> Lang.ERROR_MUST_SPECIFY_PLAYER);
    public static final Reason NO_SUCH_PLAYER = new Reason(() -> Lang.ERROR_NO_SUCH_PLAYER);

    /**
     * Construct a new MapWorldParseException
     *
     * @param input  Input
     * @param reason Failure reason
     */
    public PlayerParseException(@Nullable String input, @NotNull Reason reason) {
        super(input, "<player>", reason);
    }
}
