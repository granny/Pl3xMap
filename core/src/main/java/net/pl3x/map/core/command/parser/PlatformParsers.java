package net.pl3x.map.core.command.parser;

import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.player.Player;
import org.incendo.cloud.context.CommandContext;
import org.incendo.cloud.parser.ParserDescriptor;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

@NullMarked
public interface PlatformParsers {
    ParserDescriptor<Sender, ?> columnPosParser();

    Point resolvePointFromColumnPos(String name, CommandContext<Sender> context);

    ParserDescriptor<Sender, ?> playerSelectorParser();

    @Nullable Player resolvePlayerFromPlayerSelector(String name, CommandContext<Sender> context);
}
