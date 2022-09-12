package net.pl3x.map.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.parser.PlayerParser;
import net.pl3x.map.command.exception.PlayerParseException;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.Player;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Player} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
public class PlayerArgument<C> extends CommandArgument<C, Player> {
    protected PlayerArgument(boolean required, String name, String defaultValue, BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider, ArgumentDescription defaultDescription) {
        super(required, name, new PlayerParser<>(), defaultValue, Player.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link PlayerArgument} builder.
     *
     * @param name argument name
     * @return new player argument builder
     */
    public static <C> CommandArgument.Builder<C, Player> newBuilder(String name) {
        return new PlayerArgument.Builder<>(name);
    }

    /**
     * Create a required {@link PlayerArgument}.
     *
     * @param name argument name
     * @return constructed player argument
     */
    public static <C> CommandArgument<C, Player> of(String name) {
        return PlayerArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create an optional {@link PlayerArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed player argument
     */
    public static <C> CommandArgument<C, Player> optional(String name) {
        return PlayerArgument.<C>newBuilder(name).asOptional().build();
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
    public static <C> CommandArgument<C, Player> optional(String name, String defaultValue) {
        return PlayerArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
    }

    /**
     * Resolve {@link Player} from command context.
     * <p>
     * If context does not contain a {@link Player} and the sender is a {@link Player} then the sender will be used.
     *
     * @param context command context
     * @param name    argument name
     * @return player
     * @throws PlayerParseException if context did not contain a {@link Player}
     *                              and the sender is not a {@link Player}
     */
    public static Player resolve(CommandContext<Sender> context, String name) {
        Sender sender = context.getSender();
        Player player = context.getOrDefault(name, null);
        if (player != null) {
            return player;
        }
        if (sender instanceof Player) {
            return (Player) sender;
        }
        sender.send(Lang.ERROR_MUST_SPECIFY_PLAYER);
        throw new PlayerParseException(null, PlayerParseException.MUST_SPECIFY_PLAYER);
    }

    /**
     * Mutable builder for {@link PlayerArgument} instances.
     *
     * @param <C> command sender type
     */
    public static class Builder<C> extends CommandArgument.Builder<C, Player> {
        private Builder(String name) {
            super(Player.class, name);
        }

        @Override
        @NotNull
        public CommandArgument<C, Player> build() {
            return new PlayerArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
