package net.pl3x.map.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.exception.PointParseException;
import net.pl3x.map.markers.Point;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link Point}s.
 *
 * @param <C> command sender type
 */
public class PointParser<C> implements ArgumentParser<C, Point> {
    @Override
    @NotNull
    public ArgumentParseResult<Point> parse(@NotNull CommandContext<C> context, Queue<String> queue) {
        if (queue.size() < 2) {
            StringBuilder input = new StringBuilder();
            for (int i = 0; i < queue.size(); i++) {
                input.append(((LinkedList<String>) queue).get(i));
            }
            return failure(new PointParseException(input.toString(), PointParseException.INVALID_FORMAT));
        }
        Integer[] coordinates = new Integer[2];
        for (int i = 0; i < 2; i++) {
            ArgumentParseResult<Integer> coordinate = parseCoord(context, queue);
            if (coordinate.getFailure().isPresent()) {
                return failure(coordinate.getFailure().get());
            }
            coordinates[i] = coordinate.getParsedValue().orElseThrow(NullPointerException::new);
        }

        return success(new Point(coordinates[0], coordinates[1]));
    }

    @Override
    @NotNull
    public List<String> suggestions(@NotNull CommandContext<C> commandContext, @NotNull String input) {
        return Pl3xMap.api().getWorldRegistry()
                .entries().values().stream()
                .map(World::getName)
                .collect(Collectors.toList());
    }

    @NotNull
    public ArgumentParseResult<Integer> parseCoord(CommandContext<C> context, Queue<String> queue) {
        String input = queue.peek();
        if (input == null) {
            return failure(new NoInputProvidedException(PointParser.class, context));
        }

        int coordinate;
        try {
            coordinate = input.isEmpty() ? 0 : Integer.parseInt(input);
        } catch (Exception e) {
            e.printStackTrace();
            return failure(new IntegerArgument.IntegerParseException(
                    input,
                    new IntegerArgument.IntegerParser<>(
                            IntegerArgument.IntegerParser.DEFAULT_MINIMUM,
                            IntegerArgument.IntegerParser.DEFAULT_MAXIMUM
                    ),
                    context
            ));
        }

        queue.remove();
        return success(coordinate);
    }
}
