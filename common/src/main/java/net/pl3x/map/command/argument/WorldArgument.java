package net.pl3x.map.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.parser.WorldParser;
import net.pl3x.map.command.exception.WorldParseException;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.Player;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link World} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
public class WorldArgument<C> extends CommandArgument<C, World> {
    protected WorldArgument(boolean required, String name, String defaultValue, BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider, ArgumentDescription defaultDescription) {
        super(required, name, new WorldParser<>(), defaultValue, World.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link WorldArgument} builder.
     *
     * @param name argument name
     * @return new world argument builder
     */
    public static <C> CommandArgument.Builder<C, World> builder(String name) {
        return new WorldArgument.Builder<>(name);
    }

    /**
     * Create a required {@link WorldArgument}.
     *
     * @param name argument name
     * @return constructed world argument
     */
    public static <C> CommandArgument<C, World> of(String name) {
        return WorldArgument.<C>builder(name).asRequired().build();
    }

    /**
     * Create an optional {@link WorldArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed world argument
     */
    public static <C> CommandArgument<C, World> optional(String name) {
        return WorldArgument.<C>builder(name).asOptional().build();
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
    public static <C> CommandArgument<C, World> optional(String name, String defaultValue) {
        return WorldArgument.<C>builder(name).asOptionalWithDefault(defaultValue).build();
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
    public static World resolve(CommandContext<Sender> context, String name) {
        Sender sender = context.getSender();
        World world = context.getOrDefault(name, null);
        if (world != null) {
            return world;
        }
        if (sender instanceof Player player) {
            world = player.getWorld();
            if (!world.isEnabled()) {
                throw new WorldParseException(world.getName(), WorldParseException.MAP_NOT_ENABLED);
            } else {
                return world;
            }
        }
        sender.send(Lang.ERROR_MUST_SPECIFY_WORLD);
        throw new WorldParseException(null, WorldParseException.MUST_SPECIFY_WORLD);
    }

    /**
     * Mutable builder for {@link WorldArgument} instances.
     *
     * @param <C> command sender type
     */
    public static class Builder<C> extends CommandArgument.Builder<C, World> {
        private Builder(String name) {
            super(World.class, name);
        }

        @Override
        @NotNull
        public CommandArgument<C, World> build() {
            return new WorldArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
