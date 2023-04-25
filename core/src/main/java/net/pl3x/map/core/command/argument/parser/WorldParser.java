package net.pl3x.map.core.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.exception.WorldParseException;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link World}s.
 *
 * @param <C> command sender type
 */
public class WorldParser<@NonNull C> implements ArgumentParser<@NonNull C, @NonNull World> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull World> parse(@NonNull CommandContext<@NonNull C> context, @NonNull Queue<@NonNull String> queue) {
        String input = queue.peek();
        if (input == null) {
            return failure(new WorldParseException(null, WorldParseException.MUST_SPECIFY_WORLD));
        }

        World world = null;
        try {
            world = Pl3xMap.api().getWorldRegistry().get(input);
        } catch (Throwable ignore) {
        }

        if (world == null) {
            return failure(new WorldParseException(input, WorldParseException.NO_SUCH_WORLD));
        }

        if (!world.isEnabled()) {
            return failure(new WorldParseException(input, WorldParseException.MAP_NOT_ENABLED));
        }

        queue.remove();
        return success(world);
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<@NonNull C> commandContext, @NonNull String input) {
        return Pl3xMap.api().getWorldRegistry()
                .values().stream()
                .filter(World::isEnabled)
                .map(World::getName)
                .collect(Collectors.toList());
    }
}
