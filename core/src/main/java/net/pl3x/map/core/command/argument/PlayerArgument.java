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
import net.pl3x.map.core.command.argument.parser.PlayerParser;
import net.pl3x.map.core.command.exception.PlayerParseException;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * A {@link Player} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
public class PlayerArgument<@NonNull C> extends CommandArgument<@NonNull C, @NonNull Player> {
    protected PlayerArgument(boolean required, @NonNull String name, @NonNull String defaultValue, @NonNull BiFunction<@NonNull CommandContext<@NonNull C>, @NonNull String, @NonNull List<@NonNull String>> suggestionsProvider, @NonNull ArgumentDescription defaultDescription) {
        super(required, name, new PlayerParser<>(), defaultValue, Player.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link PlayerArgument} builder.
     *
     * @param name argument name
     * @return new player argument builder
     */
    public static <@NonNull C> CommandArgument.@NonNull Builder<@NonNull C, @NonNull Player> newBuilder(@NonNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a required {@link PlayerArgument}.
     *
     * @param name argument name
     * @return constructed player argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull Player> of(@NonNull String name) {
        return PlayerArgument.<@NonNull C>newBuilder(name).asRequired().build();
    }

    /**
     * Create an optional {@link PlayerArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed player argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull Player> optional(@NonNull String name) {
        return PlayerArgument.<@NonNull C>newBuilder(name).asOptional().build();
    }

    /**
     * Create an optional {@link PlayerArgument} with a default value.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name         argument name
     * @param defaultValue default value that will be used if none was supplied
     * @return constructed player argument
     */
    public static <@NonNull C> @NonNull CommandArgument<@NonNull C, @NonNull Player> optional(@NonNull String name, @NonNull String defaultValue) {
        return PlayerArgument.<@NonNull C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
    }

    /**
     * Resolve {@link Player} from command context.
     * <p>
     * If context does not contain a {@link Player} and the sender is a {@link net.pl3x.map.core.command.Sender.Player} then the sender will be used.
     *
     * @param context command context
     * @param name    argument name
     * @return player
     * @throws PlayerParseException if context did not contain a {@link Player} and the sender is not a {@link net.pl3x.map.core.command.Sender.Player}
     */
    public static @NonNull Player resolve(@NonNull CommandContext<@NonNull Sender> context, @NonNull String name) {
        Sender sender = context.getSender();
        Player player = context.getOrDefault(name, null);
        if (player != null) {
            return player;
        }
        if (sender instanceof Sender.Player<?> senderPlayer) {
            player = Pl3xMap.api().getPlayerRegistry().get(senderPlayer.getUUID());
            if (player != null) {
                return player;
            }
        }
        sender.sendMessage(Lang.ERROR_MUST_SPECIFY_PLAYER);
        throw new PlayerParseException(null, PlayerParseException.MUST_SPECIFY_PLAYER);
    }

    /**
     * Mutable builder for {@link PlayerArgument} instances.
     *
     * @param <C> command sender type
     */
    public static class Builder<@NonNull C> extends CommandArgument.Builder<@NonNull C, @NonNull Player> {
        private Builder(@NonNull String name) {
            super(Player.class, name);
        }

        @Override
        public @NonNull CommandArgument<@NonNull C, @NonNull Player> build() {
            return new PlayerArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
