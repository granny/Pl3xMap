package net.pl3x.map.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
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
        String input = queue.peek();
        if (input == null) {
            return failure(new NoInputProvidedException(PointParser.class, context));
        }

        Point point;
        try {
            String[] nums = input.split(" ");
            int x = Integer.parseInt(nums[0]);
            int z = Integer.parseInt(nums[1]);
            point = Point.of(x, z);
        } catch (Throwable e) {
            return failure(new PointParseException(input, PointParseException.INVALID_FORMAT));
        }

        queue.remove();
        return success(point);
    }

    @Override
    @NotNull
    public List<String> suggestions(@NotNull CommandContext<C> commandContext, @NotNull String input) {
        return Pl3xMap.api().getWorldRegistry()
                .entries().values().stream()
                .map(World::getName)
                .collect(Collectors.toList());
    }
}
