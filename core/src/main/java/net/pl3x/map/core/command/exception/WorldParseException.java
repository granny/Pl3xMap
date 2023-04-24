package net.pl3x.map.core.command.exception;

import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Thrown to indicate that a method has been passed an illegal or inappropriate {@link World} argument.
 */
public class WorldParseException extends ArgumentParseException {
    public static final Reason MUST_SPECIFY_WORLD = new Reason(() -> Lang.ERROR_MUST_SPECIFY_WORLD);
    public static final Reason NO_SUCH_WORLD = new Reason(() -> Lang.ERROR_NO_SUCH_WORLD);
    public static final Reason MAP_NOT_ENABLED = new Reason(() -> Lang.ERROR_WORLD_DISABLED);

    /**
     * Construct a new WorldParseException.
     *
     * @param input  Input
     * @param reason Failure reason
     */
    public WorldParseException(@Nullable String input, @NotNull Reason reason) {
        super(input, "<world>", reason);
    }
}
