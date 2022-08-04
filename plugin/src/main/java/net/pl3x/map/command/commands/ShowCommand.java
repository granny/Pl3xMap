package net.pl3x.map.command.commands;

import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.api.player.MapPlayer;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.CommandSender;

public class ShowCommand extends Pl3xMapCommand {
    public ShowCommand(Pl3xMapPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("show")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_SHOW_DESCRIPTION))
                .permission("pl3xmap.command.show")
                .handler(this::execute));
        getCommandManager().registerSubcommand(builder -> builder.literal("show")
                .argument(SinglePlayerSelectorArgument.optional("player"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_PLAYER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_SHOW_DESCRIPTION))
                .permission("pl3xmap.command.show.others")
                .handler(this::execute));
    }

    private void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapPlayer target = resolvePlayer(context);

        if (!target.isHidden()) {
            Lang.send(sender, Lang.COMMAND_SHOW_NOT_HIDDEN, Placeholder.unparsed("player", target.getName()));
            return;
        }
        target.setHidden(false, true);
        Lang.send(sender, Lang.COMMAND_SHOW_SUCCESS, Placeholder.unparsed("player", target.getName()));
    }
}
