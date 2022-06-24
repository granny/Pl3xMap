package net.pl3x.map.command;

import cloud.commandframework.bukkit.arguments.selector.SinglePlayerSelector;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.command.exception.CompletedSuccessfullyException;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.WorldManager;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandHelper {
    private CommandHelper() {
    }

    public static MapWorld resolveWorld(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld world = context.getOrDefault("world", null);
        if (world != null) {
            return world;
        }
        if (sender instanceof Player player) {
            World bukkit = player.getWorld();
            MapWorld mapWorld = WorldManager.INSTANCE.getMapWorld(bukkit);
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

    public static Player resolvePlayer(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        SinglePlayerSelector selector = context.getOrDefault("player", null);

        if (selector == null) {
            if (sender instanceof Player) {
                return (Player) sender;
            }
            Lang.send(sender, Lang.ERROR_MUST_SPECIFY_PLAYER);
            throw new CompletedSuccessfullyException();
        }

        Player targetPlayer = selector.getPlayer();
        if (targetPlayer == null) {
            Lang.send(sender, Lang.ERROR_NO_SUCH_PLAYER,
                    Placeholder.unparsed("player", selector.getSelector()));
            throw new CompletedSuccessfullyException();
        }

        return targetPlayer;
    }

    public static RichDescription description(String miniMessage, TagResolver.Single... placeholders) {
        return RichDescription.of(MiniMessage.miniMessage().deserialize(miniMessage, placeholders));
    }
}
