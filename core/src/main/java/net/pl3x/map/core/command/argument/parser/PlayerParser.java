package net.pl3x.map.core.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.exception.PlayerParseException;
import net.pl3x.map.core.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link Player}s.
 *
 * @param <C> command sender type
 */
public class PlayerParser<@NonNull C> implements ArgumentParser<@NonNull C, @NonNull Player> {
    @Override
    public @NonNull ArgumentParseResult<@NonNull Player> parse(@NonNull CommandContext<@NonNull C> context, @NonNull Queue<@NonNull String> queue) {
        String input = queue.peek();
        if (input == null) {
            return failure(new PlayerParseException(null, PlayerParseException.MUST_SPECIFY_PLAYER));
        }

        Player player = Pl3xMap.api().getPlayerRegistry().get(input);
        if (player == null) {
            return failure(new PlayerParseException(input, PlayerParseException.NO_SUCH_PLAYER));
        }

        queue.remove();
        return success(player);
    }

    @Override
    public @NonNull List<@NonNull String> suggestions(@NonNull CommandContext<@NonNull C> commandContext, @NonNull String input) {
        return Pl3xMap.api().getPlayerRegistry()
                .values().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
