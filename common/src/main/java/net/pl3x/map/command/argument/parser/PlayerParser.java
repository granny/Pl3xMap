package net.pl3x.map.command.argument.parser;

import cloud.commandframework.arguments.parser.ArgumentParseResult;
import cloud.commandframework.arguments.parser.ArgumentParser;
import cloud.commandframework.context.CommandContext;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.exception.PlayerParseException;
import net.pl3x.map.player.Player;
import org.jetbrains.annotations.NotNull;

import static cloud.commandframework.arguments.parser.ArgumentParseResult.failure;
import static cloud.commandframework.arguments.parser.ArgumentParseResult.success;

/**
 * Parser that parses strings into {@link Player}s.
 *
 * @param <C> command sender type
 */
public class PlayerParser<C> implements ArgumentParser<C, Player> {
    @Override
    @NotNull
    public ArgumentParseResult<Player> parse(@NotNull CommandContext<C> context, Queue<String> queue) {
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
    @NotNull
    public List<String> suggestions(@NotNull CommandContext<C> commandContext, @NotNull String input) {
        return Pl3xMap.api().getPlayerRegistry()
                .entries().values().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
    }
}
