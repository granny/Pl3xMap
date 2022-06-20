package net.pl3x.map.command.commands;

import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.BaseCommand;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.Command;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CancelRenderCommand extends BaseCommand {
    public CancelRenderCommand(Pl3xMap plugin) {
        super(plugin, "cancelrender", Lang.COMMAND_CANCELRENDER_DESCRIPTION, "pl3xmap.command.cancelrender", "[world]");
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
        if (!mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_CANCELRENDER_NOT_RENDERING,
                    Placeholder.parsed("world", mapWorld.getName()));
            return;
        }

        mapWorld.cancelRender();

        if (sender instanceof Player) {
            Lang.send(sender, Lang.COMMAND_CANCELRENDER_SUCCESS,
                    Placeholder.parsed("world", mapWorld.getName()));
        }
    }
}
