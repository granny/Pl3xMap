package net.pl3x.map.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.command.argument.parser.PlayerParser;
import net.pl3x.map.player.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerArgument<C> extends CommandArgument<C, Player> {
    public static final String PLAYER = "player";

    protected PlayerArgument(boolean required, String name, String defaultValue, BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider, ArgumentDescription defaultDescription) {
        super(required, name, new PlayerParser<>(), defaultValue, Player.class, suggestionsProvider, defaultDescription);
    }

    public static <C> CommandArgument.Builder<C, Player> newBuilder(String name) {
        return new PlayerArgument.Builder<>(name);
    }

    public static <C> CommandArgument<C, Player> of(String name) {
        return PlayerArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> CommandArgument<C, Player> optional(String name) {
        return PlayerArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> CommandArgument<C, Player> optional(String name, String defaultValue) {
        return PlayerArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
    }

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
