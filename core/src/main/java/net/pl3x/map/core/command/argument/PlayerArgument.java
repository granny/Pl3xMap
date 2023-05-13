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
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Player} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
public class PlayerArgument<C> extends CommandArgument<@NotNull C, @NotNull Player> {
    protected PlayerArgument(boolean required, @NotNull String name, @NotNull String defaultValue, @NotNull BiFunction<@NotNull CommandContext<@NotNull C>, @NotNull String, @NotNull List<@NotNull String>> suggestionsProvider, @NotNull ArgumentDescription defaultDescription) {
        super(required, name, new PlayerParser<>(), defaultValue, Player.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link PlayerArgument} builder.
     *
     * @param name argument name
     * @return new player argument builder
     */
    public static <C> CommandArgument.@NotNull Builder<@NotNull C, @NotNull Player> newBuilder(@NotNull String name) {
        return new Builder<>(name);
    }

    /**
     * Create a required {@link PlayerArgument}.
     *
     * @param name argument name
     * @return constructed player argument
     */
    public static <C> @NotNull CommandArgument<@NotNull C, @NotNull Player> of(@NotNull String name) {
        return PlayerArgument.<@NotNull C>newBuilder(name).asRequired().build();
    }

    /**
     * Create an optional {@link PlayerArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed player argument
     */
    public static <C> @NotNull CommandArgument<@NotNull C, @NotNull Player> optional(@NotNull String name) {
        return PlayerArgument.<@NotNull C>newBuilder(name).asOptional().build();
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
    public static <C> @NotNull CommandArgument<@NotNull C, @NotNull Player> optional(@NotNull String name, @NotNull String defaultValue) {
        return PlayerArgument.<@NotNull C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
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
    public static @NotNull Player resolve(@NotNull CommandContext<@NotNull Sender> context, @NotNull String name) {
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
    public static class Builder<C> extends CommandArgument.Builder<@NotNull C, @NotNull Player> {
        private Builder(@NotNull String name) {
            super(Player.class, name);
        }

        @Override
        public @NotNull CommandArgument<@NotNull C, @NotNull Player> build() {
            return new PlayerArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
