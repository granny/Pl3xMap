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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import net.pl3x.map.core.command.exception.ZoomParseException;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link Integer}s for zoom levels.
 *
 * @param <C> command sender type
 */
public class ZoomParser<C> implements ArgumentParser<C, Integer> {
    @Override
    public @NotNull ArgumentParseResult<Integer> parse(@NotNull CommandContext<C> context, @NotNull Queue<@NotNull String> inputQueue) {
        String input = inputQueue.peek();
        if (input == null) {
            return failure(new NoInputProvidedException(IntegerArgument.IntegerParser.class, context));
        }
        try {
            int zoom = Integer.parseInt(input);
            if (zoom < 0 || zoom > getMax(context)) {
                return failure(new ZoomParseException(input, ZoomParseException.NOT_VALID_ZOOM_LEVEL));
            }
            inputQueue.remove();
            return success(zoom);
        } catch (Exception e) {
            return failure(new ZoomParseException(input, ZoomParseException.NOT_VALID_ZOOM_LEVEL));
        }
    }

    @Override
    public @NotNull List<@NotNull String> suggestions(@NotNull CommandContext<C> context, @NotNull String input) {
        Set<Long> numbers = new TreeSet<>();
        try {
            long inputNum = Long.parseLong(input.equals("-") ? "-0" : input.isEmpty() ? "0" : input);
            long inputNumAbsolute = Math.abs(inputNum);
            numbers.add(inputNumAbsolute); /* It's a valid number, so we suggest it */
            int max = getMax(context);
            for (int i = 0; i < 10 && (inputNum * 10) + i <= max; i++) {
                numbers.add((inputNumAbsolute * 10) + i);
            }
            List<String> suggestions = new LinkedList<>();
            for (long number : numbers) {
                if (input.startsWith("-")) {
                    number = -number; /* Preserve sign */
                }
                if (number < 0 || number > max) {
                    continue;
                }
                suggestions.add(String.valueOf(number));
            }
            return suggestions;
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private int getMax(@NotNull CommandContext<C> context) {
        return ((World) context.get("world")).getConfig().ZOOM_MAX_OUT;
    }
}
