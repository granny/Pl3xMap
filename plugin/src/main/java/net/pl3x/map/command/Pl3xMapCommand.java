package net.pl3x.map.command;

import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.player.MapPlayer;
import net.pl3x.map.api.player.PlayerManager;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class Pl3xMapCommand {
    private final Pl3xMapPlugin plugin;
    private final CommandManager commandManager;

    protected Pl3xMapCommand(Pl3xMapPlugin plugin, CommandManager commandManager) {
        this.plugin = plugin;
        this.commandManager = commandManager;
    }

    public Pl3xMapPlugin getPlugin() {
        return this.plugin;
    }

    public CommandManager getCommandManager() {
        return this.commandManager;
    }

    public abstract void register();

    public static MapWorld resolveWorld(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld world = context.getOrDefault("world", null);
        if (world != null) {
            return world;
        }
        if (sender instanceof Player player) {
            World bukkit = player.getWorld();
            MapWorld mapWorld = Pl3xMap.api().getWorldManager().getMapWorld(bukkit);
            if (mapWorld == null) {
                Lang.send(sender, Lang.ERROR_WORLD_DISABLED,
                        Placeholder.unparsed("world", bukkit.getName()));
                throw new CompletedSuccessfullyException();
            } else {
                return mapWorld;
            }
        } else {
            Lang.send(sender, Lang.ERROR_MUST_SPECIFY_WORLD);
            throw new CompletedSuccessfullyException();
        }
    }

    public static MapPlayer resolvePlayer(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        SinglePlayerSelector selector = context.getOrDefault("player", null);
        PlayerManager playerManager = Pl3xMap.api().getPlayerManager();

        if (selector == null) {
            if (sender instanceof Player player) {
                return playerManager.getPlayer(player.getUniqueId());
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

        return playerManager.getPlayer(target.getUniqueId());
    }

    public static RichDescription description(String miniMessage, TagResolver.Single... placeholders) {
        return RichDescription.of(Lang.parse(miniMessage, placeholders));
    }

    public static class CompletedSuccessfullyException extends IllegalArgumentException {
    }
}
