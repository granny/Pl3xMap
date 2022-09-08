package net.pl3x.map.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.command.argument.parser.PointParser;
import net.pl3x.map.markers.Point;
import org.jetbrains.annotations.NotNull;

public class PointArgument<C> extends CommandArgument<C, Point> {
    protected PointArgument(boolean required, String name, String defaultValue, BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider, ArgumentDescription defaultDescription) {
        super(required, name, new PointParser<>(), defaultValue, Point.class, suggestionsProvider, defaultDescription);
    }

    public static <C> CommandArgument.Builder<C, Point> newBuilder(String name) {
        return new PointArgument.Builder<>(name);
    }

    public static <C> CommandArgument<C, Point> of(String name) {
        return PointArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> CommandArgument<C, Point> optional(String name) {
        return PointArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> CommandArgument<C, Point> optional(String name, String defaultValue) {
        return PointArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
    }

    public static class Builder<C> extends CommandArgument.Builder<C, Point> {
        private Builder(String name) {
            super(Point.class, name);
        }

        @Override
        @NotNull
        public CommandArgument<C, Point> build() {
            return new PointArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }
}
