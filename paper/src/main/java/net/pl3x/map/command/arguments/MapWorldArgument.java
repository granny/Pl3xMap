package net.pl3x.map.command.arguments;

import cloud.commandframework.ArgumentDescription;
import cloud.commandframework.arguments.CommandArgument;
import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.exceptions.parsing.NoInputProvidedException;
import java.util.List;
import java.util.Queue;
import java.util.function.BiFunction;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.BukkitWorld;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

public class MapWorldArgument<C> extends CommandArgument<C, MapWorld> {
    protected MapWorldArgument(boolean required, String name, String defaultValue, BiFunction<CommandContext<C>, String, List<String>> suggestionsProvider, ArgumentDescription defaultDescription) {
        super(required, name, new MapWorldParser<>(), defaultValue, MapWorld.class, suggestionsProvider, defaultDescription);
    }

    public static <C> CommandArgument.Builder<C, MapWorld> newBuilder(String name) {
        return new MapWorldArgument.Builder<>(name);
    }

    public static <C> CommandArgument<C, MapWorld> of(String name) {
        return MapWorldArgument.<C>newBuilder(name).asRequired().build();
    }

    public static <C> CommandArgument<C, MapWorld> optional(String name) {
        return MapWorldArgument.<C>newBuilder(name).asOptional().build();
    }

    public static <C> CommandArgument<C, MapWorld> optional(String name, String defaultValue) {
        return MapWorldArgument.<C>newBuilder(name).asOptionalWithDefault(defaultValue).build();
    }

    public static class Builder<C> extends CommandArgument.Builder<C, MapWorld> {
        private Builder(String name) {
            super(MapWorld.class, name);
        }

        @Override
        public @NotNull CommandArgument<C, MapWorld> build() {
            return new MapWorldArgument<>(isRequired(), getName(), getDefaultValue(), getSuggestionsProvider(), getDefaultDescription());
        }
    }

    public static class MapWorldParser<C> implements ArgumentParser<C, MapWorld> {
        @Override
        public @NotNull ArgumentParseResult<MapWorld> parse(@NotNull CommandContext<C> commandContext, Queue<String> inputQueue) {
            String input = inputQueue.peek();
            if (input == null) {
                return ArgumentParseResult.failure(new NoInputProvidedException(MapWorldParser.class, commandContext));
            }

            World world = Bukkit.getWorld(input);
            if (world == null) {
                return ArgumentParseResult.failure(new MapWorldParseException(input, MapWorldParseException.FailureReason.NO_SUCH_WORLD));
            }

            MapWorld mapWorld = Pl3xMap.api().getWorldRegistry().get(new BukkitWorld(world).getKey());
            if (mapWorld == null) {
                return ArgumentParseResult.failure(new MapWorldParseException(input, MapWorldParseException.FailureReason.MAP_NOT_ENABLED));
            }

            if (!mapWorld.getConfig().ENABLED) {
                return ArgumentParseResult.failure(new MapWorldParseException(input, MapWorldParseException.FailureReason.MAP_NOT_ENABLED));
            }

            inputQueue.remove();
            return ArgumentParseResult.success(mapWorld);
        }

        @Override
        public @NotNull List<String> suggestions(@NotNull CommandContext<C> commandContext, @NotNull String input) {
            return Pl3xMap.api().getWorldRegistry().entries().values().stream().map(mapWorld -> mapWorld.getWorld().getName()).collect(Collectors.toList());
        }
    }

    public static class MapWorldParseException extends IllegalArgumentException {
        private final String input;
        private final FailureReason reason;

        /**
         * Construct a new MapWorldParseException
         *
         * @param input  Input
         * @param reason Failure reason
         */
        public MapWorldParseException(String input, FailureReason reason) {
            this.input = input;
            this.reason = reason;
        }

        @Override
        public String getMessage() {
            return MiniMessage.miniMessage().stripTags(this.reason.toString()).replace("<world>", this.input);
        }

        public enum FailureReason {
            NO_SUCH_WORLD(() -> Lang.ERROR_NO_SUCH_WORLD),
            MAP_NOT_ENABLED(() -> Lang.ERROR_WORLD_DISABLED);

            private final Supplier<String> supplier;

            FailureReason(Supplier<String> supplier) {
                this.supplier = supplier;
            }

            @Override
            public String toString() {
                return this.supplier.get();
            }
        }
    }
}
