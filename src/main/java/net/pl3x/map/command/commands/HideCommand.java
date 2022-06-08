package net.pl3x.map.command.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.BaseCommand;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.PlayerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class HideCommand extends BaseCommand {
    public HideCommand(Pl3xMap plugin) {
        super(plugin, "hide", Lang.CMD_HIDE_DESCRIPTION, "pl3xmap.command.hide", "/<command> hide (player)");
    }

    @Override
    public List<String> handleTabComplete(CommandSender sender, Command command, LinkedList<String> args) {
        if (args.size() > 0 && sender.hasPermission("pl3xmap.command.hide.others")) {
            return tabPlayers(args.peek());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean handleCommand(CommandSender sender, Command command, LinkedList<String> args) throws CommandException {
        Player target = getPlayer(sender, args, "pl3xmap.command.hide.others");
        PlayerManager playerManager = getPlugin().getPlayerManager();
        if (playerManager.isHidden(target)) {
            Lang.send(sender, Lang.CMD_HIDE_ALREADY_HIDDEN, Placeholder.parsed("player", target.getName()));
            return true;
        }
        playerManager.setHidden(target, true, true);
        Lang.send(sender, Lang.CMD_HIDE_SUCCESS, Placeholder.parsed("player", target.getName()));
        return true;
    }
}
