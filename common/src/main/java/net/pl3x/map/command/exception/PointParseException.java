package net.pl3x.map.command.exception;

import net.pl3x.map.configuration.Lang;
import net.pl3x.map.markers.Point;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown to indicate that a method has been passed an illegal or inappropriate {@link Point} argument.
 */
public class PointParseException extends ArgumentParseException {
    public static final Reason INVALID_FORMAT = new Reason(() -> Lang.ERROR_POINT_INVALID_FORMAT);

    /**
     * Construct a new PointParseException.
     *
     * @param input  Input
     * @param reason Failure reason
     */
    public PointParseException(@Nullable String input, @NotNull Reason reason) {
        super(input, "<point>", reason);
    }
}
