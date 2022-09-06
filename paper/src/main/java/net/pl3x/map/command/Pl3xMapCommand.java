package net.pl3x.map.command;

import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.MapPlayer;
import net.pl3x.map.player.PlayerRegistry;
import net.pl3x.map.world.BukkitWorld;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Pl3xMapCommand {
    private final PaperPl3xMap plugin;
    private final CommandManager commandManager;

    protected Pl3xMapCommand(PaperPl3xMap plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public PaperPl3xMap getPlugin() {
        return this.plugin;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public abstract void register();

    public static MapWorld resolveWorld(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = context.getOrDefault("world", null);
        if (mapWorld != null) {
            return mapWorld;
        }
        if (sender instanceof Player player) {
            World world = new BukkitWorld(player.getWorld());
            MapWorld playerWorld = Pl3xMap.api().getWorldRegistry().get(world.getKey());
            if (playerWorld == null) {
                throw new MapWorldArgument.MapWorldParseException(world.getName(), MapWorldArgument.MapWorldParseException.FailureReason.NO_SUCH_WORLD);
            } else if (!playerWorld.getConfig().ENABLED) {
                throw new MapWorldArgument.MapWorldParseException(world.getName(), MapWorldArgument.MapWorldParseException.FailureReason.MAP_NOT_ENABLED);
            } else {
                return playerWorld;
            }
        } else {
            Lang.send(sender, Lang.ERROR_MUST_SPECIFY_WORLD);
            throw new CompletedSuccessfullyException();
        }
    }

    public static MapPlayer resolvePlayer(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        SinglePlayerSelector selector = context.getOrDefault("player", null);
        PlayerRegistry registry = Pl3xMap.api().getPlayerRegistry();

        if (selector == null) {
            if (sender instanceof Player player) {
                return registry.getPlayer(player.getUniqueId());
            }
            Lang.send(sender, Lang.ERROR_MUST_SPECIFY_PLAYER);
            throw new CompletedSuccessfullyException();
        }

        Player target = selector.getPlayer();
        if (target == null) {
            Lang.send(sender, Lang.ERROR_NO_SUCH_PLAYER,
                    Placeholder.unparsed("player", selector.getSelector()));
            throw new CompletedSuccessfullyException();
        }

        return registry.getPlayer(target.getUniqueId());
    }

    public static RichDescription description(String miniMessage, TagResolver.Single... placeholders) {
        return RichDescription.of(Lang.parse(miniMessage, placeholders));
    }

    public static class CompletedSuccessfullyException extends IllegalArgumentException {
    }
}
