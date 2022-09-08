package net.pl3x.map.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.command.argument.parser.WorldParser;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public class WorldArgument<C> extends CommandArgument<C, World> {
    public static final String WORLD = "world";

    protected WorldArgument(boolean required, String name, String defaultValue, BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider, ArgumentDescription defaultDescription) {
        super(required, name, new WorldParser<>(), defaultValue, World.class, suggestionsProvider, defaultDescription);
    }

    public static <C> CommandArgument.Builder<C, World> newBuilder(String name) {
        return new WorldArgument.Builder<>(name);
    }

    public static <C> CommandArgument<C, World> of(String name) {
        return WorldArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> CommandArgument<C, World> optional(String name) {
        return WorldArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> CommandArgument<C, World> optional(String name, String defaultValue) {
        return WorldArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
    }

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
