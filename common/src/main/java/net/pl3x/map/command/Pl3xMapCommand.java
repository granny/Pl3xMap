package net.pl3x.map.command;


import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.RichDescription;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.command.argument.PlayerArgument;
import net.pl3x.map.command.argument.WorldArgument;
import net.pl3x.map.command.exception.WorldParseException;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.Player;
import net.pl3x.map.world.World;

public abstract class Pl3xMapCommand {
    private final CommandHandler handler;

    protected Pl3xMapCommand(CommandHandler handler) {
        this.handler = handler;
    }

    public CommandHandler getHandler() {
        return this.handler;
    }

    public abstract void register();

    protected static RichDescription description(String miniMessage, TagResolver.Single... placeholders) {
        return RichDescription.of(Lang.parse(miniMessage, placeholders));
    }

    public static World resolveWorld(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        World world = context.getOrDefault(WorldArgument.WORLD, null);
        if (world != null) {
            return world;
        }
        if (sender instanceof Player player) {
            world = player.getWorld();
            if (!world.getConfig().ENABLED) {
                throw new WorldParseException(world.getName(), WorldParseException.MAP_NOT_ENABLED);
            } else {
                return world;
            }
        } else {
            sender.send(Lang.ERROR_MUST_SPECIFY_WORLD);
            throw new CompletedSuccessfullyException();
        }
    }

    public static Player resolvePlayer(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        Player player = context.getOrDefault(PlayerArgument.PLAYER, null);

        if (player == null) {
            if (sender instanceof Player) {
                return (Player) sender;
            }
            sender.send(Lang.ERROR_MUST_SPECIFY_PLAYER);
            throw new CompletedSuccessfullyException();
        }

        return player;
    }

    public static class CompletedSuccessfullyException extends IllegalArgumentException {
    }
}
