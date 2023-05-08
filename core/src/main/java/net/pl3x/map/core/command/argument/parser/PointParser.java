/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import net.pl3x.map.core.command.exception.PointParseException;
import net.pl3x.map.core.markers.Point;
import org.checkerframework.checker.nullness.qual.NonNull;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link Point}s.
 *
 * @param <C> command sender type
 */
public class PointParser<@NonNull C> implements ArgumentParser<@NonNull C, @NonNull Point> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull Point> parse(@NonNull CommandContext<@NonNull C> context, @NonNull Queue<@NonNull String> queue) {
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
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<@NonNull C> commandContext, @NonNull String input) {
        return IntegerArgument.IntegerParser.getSuggestions(Integer.MIN_VALUE, Integer.MAX_VALUE, input);
    }

    public @NonNull ArgumentParseResult<@NonNull Integer> parseCoord(@NonNull CommandContext<@NonNull C> context, @NonNull Queue<@NonNull String> queue) {
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
