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
package net.pl3x.map.core.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.parser.PointParser;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link Point} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
@SuppressWarnings("unused")
public class PointArgument<@NonNull C> extends CommandArgument<@NonNull C, @NonNull Point> {
    protected PointArgument(boolean required, @NonNull String name, @NonNull String defaultValue, @NonNull BiFunction<@NonNull CommandContext<@NonNull C>, @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider, @NonNull ArgumentDescription defaultDescription) {
        super(required, name, new PointParser<>(), defaultValue, Point.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link PointArgument} builder.
     *
     * @param name argument name
     * @return new point argument builder
     */
    public static <@NonNull C> CommandArgument.@NonNull Builder<@NonNull C, @NonNull Point> builder(@NonNull String name) {
        return new PointArgument.Builder<>(name);
    }

    /**
     * Create a required {@link PointArgument}.
     *
     * @param name argument name
     * @return constructed point argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull Point> of(@NonNull String name) {
        return PointArgument.<@NonNull C>builder(name).asRequired().build();
    }

    /**
     * Create an optional {@link PointArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed point argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull Point> optional(@NonNull String name) {
        return PointArgument.<@NonNull C>builder(name).asOptional().build();
    }

    /**
     * Create an optional {@link PointArgument} with a default value.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name         argument name
     * @param defaultValue default value that will be used if none was supplied
     * @return constructed point argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull Point> optional(@NonNull String name, @NonNull String defaultValue) {
        return PointArgument.<@NonNull C>builder(name).asOptionalWithDefault(defaultValue).build();
    }

    /**
     * Resolve {@link Point} from command context.
     * <p>
     * If context does not contain a {@link Point} and the sender is a {@link net.pl3x.map.core.command.Sender.Player} then the sender's location will be used, otherwise <code>[0, 0]</code> will be used
     *
     * @param context command context
     * @param name    argument name
     * @return player
     */
    public static @NonNull Point resolve(@NonNull CommandContext<@NonNull Sender> context, @NonNull String name) {
        Sender sender = context.getSender();
        Point point = context.getOrDefault(name, null);
        if (point != null) {
            return point;
        }
        if (sender instanceof Sender.Player) {
            Player player = Pl3xMap.api().getPlayerRegistry().get(((Sender.Player<?>) sender).getUUID());
            point = player == null ? Point.ZERO : player.getPosition();
        } else {
            point = Point.ZERO;
        }
        return point;
    }

    /**
     * Mutable builder for {@link PointArgument} instances.
     *
     * @param <C> command sender type
     */
    public static class Builder<@NonNull C> extends CommandArgument.Builder<@NonNull C, @NonNull Point> {
        private Builder(@NonNull String name) {
            super(Point.class, name);
        }

        @Override
        public @NonNull CommandArgument<@NonNull C, @NonNull Point> build() {
            return new PointArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
