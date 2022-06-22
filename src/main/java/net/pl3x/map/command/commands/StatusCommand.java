package net.pl3x.map.command.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.BaseCommand;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.progress.Progress;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatusCommand extends BaseCommand {
    public StatusCommand(Pl3xMap plugin) {
        super(plugin, "status", Lang.COMMAND_STATUS_DESCRIPTION, "pl3xmap.command.status", "[world] (chat, bossbar)");
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, LinkedList<String> args) {
        if (args != null) {
            if (args.size() == 1) {
                return tabMapWorlds(args.get(0));
            } else if (args.size() == 2) {
                return Stream.of("chat", "bossbar")
                        .filter(s -> s.startsWith(args.get(1).toLowerCase(Locale.ROOT)))
                        .collect(Collectors.toList());
            }
        }
        return Collections.emptyList();
    }

    @Override
    protected void handleCommand(CommandSender sender, Command command, String label, LinkedList<String> args) throws CommandException {
        MapWorld mapWorld = getMapWorld(sender, args);

        // if we're not actively rendering anything, background is working
        if (!mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_STATUS_NOT_RENDERING,
                    Placeholder.parsed("world", mapWorld.getName())
            );
            return;
        }

        AbstractRender render = mapWorld.getActiveRender();
        Progress progress = render.getProgress();

        // are we specifying a toggle?
        String toggle = null;
        if (args.size() == 1) {
            toggle = args.get(0);
        } else if (args.size() == 2) {
            toggle = args.get(1);
        }

        // toggle it
        if (toggle != null) {
            String arg = toggle.toLowerCase(Locale.ROOT);
            if (arg.equals("chat")) {
                if (!progress.hide(sender)) {
                    progress.show(sender);
                }
                return;
            } else if (arg.equals("bossbar")) {
                // only players can use bossbars (obviously)
                if (!(sender instanceof Player player)) {
                    Lang.send(sender, Lang.COMMAND_STATUS_PLAYER_ONLY_FEATURE);
                    return;
                }
                if (!progress.getBossbar().hide(player)) {
                    progress.getBossbar().show(player);
                }
                return;
            }
        }

        // no toggle? fine, show current status
        Lang.send(sender, Lang.COMMAND_STATUS_RENDERING,
                Placeholder.parsed("world", mapWorld.getName()),
                Placeholder.parsed("type", render.getType()),
                Placeholder.parsed("status", "<green>Running"),
                Placeholder.parsed("chunks_done", Long.toString(progress.getProcessedChunks().get())),
                Placeholder.parsed("chunks_total", Long.toString(progress.getTotalChunks())),
                Placeholder.parsed("percent", String.format("%.2f", progress.getPercent())),
                Placeholder.parsed("remaining", "1:23:45"),
                Placeholder.parsed("cps", String.format("<gold>%.2f", progress.getCPS()))
        );
    }
}
