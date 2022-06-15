package net.pl3x.map.command.commands;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.BaseCommand;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class StatusCommand extends BaseCommand {
    public StatusCommand(Pl3xMap plugin) {
        super(plugin, "status", Lang.COMMAND_STATUS_DESCRIPTION, "pl3xmap.command.status", "[world]");
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, LinkedList<String> args) {
        if (args != null) {
            return tabMapWorlds(args.peek());
        }
        return Collections.emptyList();
    }

    @Override
    protected void handleCommand(CommandSender sender, Command command, LinkedList<String> args) throws CommandException {
        MapWorld mapWorld = getMapWorld(sender, args);
        if (mapWorld.isRendering()) {
            //
        }

        Lang.send(sender, "todo"); // TODO
    }
}
