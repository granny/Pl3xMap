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
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.parser.WorldParser;
import net.pl3x.map.core.command.exception.WorldParseException;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link World} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
@SuppressWarnings("unused")
public class WorldArgument<@NonNull C> extends CommandArgument<@NonNull C, @NonNull World> {
    protected WorldArgument(boolean required, @NonNull String name, @NonNull String defaultValue, @NonNull BiFunction<@NonNull CommandContext<@NonNull C>, @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider, @NonNull ArgumentDescription defaultDescription) {
        super(required, name, new WorldParser<>(), defaultValue, World.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link WorldArgument} builder.
     *
     * @param name argument name
     * @return new world argument builder
     */
    public static <@NonNull C> CommandArgument.@NonNull Builder<@NonNull C, @NonNull World> builder(@NonNull String name) {
        return new WorldArgument.Builder<>(name);
    }

    /**
     * Create a required {@link WorldArgument}.
     *
     * @param name argument name
     * @return constructed world argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull World> of(@NonNull String name) {
        return WorldArgument.<@NonNull C>builder(name).asRequired().build();
    }

    /**
     * Create an optional {@link WorldArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed world argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull World> optional(@NonNull String name) {
        return WorldArgument.<@NonNull C>builder(name).asOptional().build();
    }

    /**
     * Create an optional {@link WorldArgument} with a default value.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name         argument name
     * @param defaultValue default value that will be used if none was supplied
     * @return constructed world argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull World> optional(@NonNull String name, @NonNull String defaultValue) {
        return WorldArgument.<@NonNull C>builder(name).asOptionalWithDefault(defaultValue).build();
    }

    /**
     * Resolve {@link World} from command context.
     * <p>
     * If context does not contain a {@link World} and the sender is a {@link Player} then the player's world will be used.
     *
     * @param context command context
     * @param name    argument name
     * @return world
     * @throws WorldParseException if context did not contain a {@link World}
     *                             and the sender is not a {@link Player}, or
     *                             the world is not enabled
     */
    public static @NonNull World resolve(@NonNull CommandContext<@NonNull Sender> context, @NonNull String name) {
        Sender sender = context.getSender();
        World world = context.getOrDefault(name, null);
        if (world != null) {
            return world;
        }
        if (sender instanceof Sender.Player<?> player) {
            world = player.getWorld();
            if (world == null) {
                throw new WorldParseException("unknown", WorldParseException.NO_SUCH_WORLD);
            }
            if (!world.isEnabled()) {
                throw new WorldParseException(world.getName(), WorldParseException.MAP_NOT_ENABLED);
            } else {
                return world;
            }
        }
        throw new WorldParseException(null, WorldParseException.MUST_SPECIFY_WORLD);
    }

    /**
     * Mutable builder for {@link WorldArgument} instances.
     *
     * @param <C> command sender type
     */
    public static class Builder<@NonNull C> extends CommandArgument.Builder<@NonNull C, @NonNull World> {
        private Builder(@NonNull String name) {
            super(World.class, name);
        }

        @Override
        public @NonNull CommandArgument<@NonNull C, @NonNull World> build() {
            return new WorldArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
