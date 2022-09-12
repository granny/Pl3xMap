package net.pl3x.map.command.argument;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.function.BiFunction;
import net.pl3x.map.command.argument.parser.PointParser;
import net.pl3x.map.markers.Point;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link Point} argument that belongs to a command.
 *
 * @param <C> command sender type
 */
public class PointArgument<C> extends CommandArgument<C, Point> {
    protected PointArgument(boolean required, String name, String defaultValue, BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider, ArgumentDescription defaultDescription) {
        super(required, name, new PointParser<>(), defaultValue, Point.class, suggestionsProvider, defaultDescription);
    }

    /**
     * Create a new {@link PointArgument} builder.
     *
     * @param name argument name
     * @return new point argument builder
     */
    public static <C> CommandArgument.Builder<C, Point> newBuilder(String name) {
        return new PointArgument.Builder<>(name);
    }

    /**
     * Create a required {@link PointArgument}.
     *
     * @param name argument name
     * @return constructed point argument
     */
    public static <C> CommandArgument<C, Point> of(String name) {
        return PointArgument.<C>newBuilder(name).asRequired().build();
    }

    /**
     * Create an optional {@link PointArgument}.
     * <p>
     * All arguments prior to any other required argument must also be required.
     *
     * @param name argument name
     * @return constructed point argument
     */
    public static <C> CommandArgument<C, Point> optional(String name) {
        return PointArgument.<C>newBuilder(name).asOptional().build();
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
    public static <C> CommandArgument<C, Point> optional(String name, String defaultValue) {
        return PointArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
    }

    /**
     * Mutable builder for {@link PointArgument} instances.
     *
     * @param <C> command sender type
     */
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
