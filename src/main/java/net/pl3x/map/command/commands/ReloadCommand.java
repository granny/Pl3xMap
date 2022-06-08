package net.pl3x.map.command.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.BaseCommand;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class ReloadCommand extends BaseCommand {
    public ReloadCommand(Pl3xMap plugin) {
        super(plugin, "reload", Lang.CMD_RELOAD_DESCRIPTION, "pl3xmap.command.reload", "/<command> reload");
    }

    @Override
    public List<String> handleTabComplete(CommandSender sender, Command command, LinkedList<String> args) {
        return Collections.emptyList();
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, LinkedList<String> args) throws CommandException {
        getPlugin().disable();
        getPlugin().enable();

        Lang.send(sender, Lang.CMD_RELOAD_SUCCESS,
                Placeholder.parsed("name", getPlugin().getName()),
                Placeholder.parsed("version", getPlugin().getDescription().getVersion()));
        return true;
    }
}
