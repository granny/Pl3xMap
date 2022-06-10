package net.pl3x.map.command.commands;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.BaseCommand;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class FullRenderCommand extends BaseCommand {
    public FullRenderCommand(Pl3xMap plugin) {
        super(plugin, "fullrender", Lang.COMMAND_FULLRENDER_DESCRIPTION, "pl3xmap.command.fullrender", "[world]");
    }

    @Override
    protected List<String> handleTabComplete(CommandSender sender, Command command, LinkedList<String> args) {
        return Collections.emptyList();
    }

    @Override
    protected boolean handleCommand(CommandSender sender, Command command, LinkedList<String> args) throws CommandException {
        Lang.send(sender, "todo"); // TODO
        return true;
    }
}
