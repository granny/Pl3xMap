package net.pl3x.map.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.exception.WorldParseException;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link World}s.
 *
 * @param <C> command sender type
 */
public class WorldParser<C> implements ArgumentParser<C, World> {
    @Override
    @NotNull
    public ArgumentParseResult<World> parse(@NotNull CommandContext<C> context, Queue<String> queue) {
        String input = queue.peek();
        if (input == null) {
            return failure(new WorldParseException(null, WorldParseException.MUST_SPECIFY_WORLD));
        }

        World world = Pl3xMap.api().getWorldRegistry().get(input);
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
    @NotNull
    public List<String> suggestions(@NotNull CommandContext<C> commandContext, @NotNull String input) {
        return Pl3xMap.api().getWorldRegistry()
                .entries().values().stream()
                .filter(World::isEnabled)
                .map(World::getName)
                .collect(Collectors.toList());
    }
}
