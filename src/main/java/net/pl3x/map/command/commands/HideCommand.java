package net.pl3x.map.command.commands;

import cloud.commandframework.bukkit.parsers.selector.SinglePlayerSelectorArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.CommandHelper;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.PlayerManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HideCommand extends Pl3xMapCommand {
    public HideCommand(Pl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("hide")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_HIDE_DESCRIPTION))
                .permission("pl3xmap.command.hide")
                .handler(this::execute));
        getCommandManager().registerSubcommand(builder -> builder.literal("hide")
                .argument(SinglePlayerSelectorArgument.optional("player"), CommandHelper.description(Lang.COMMAND_ARGUMENT_OPTIONAL_PLAYER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_HIDE_DESCRIPTION))
                .permission("pl3xmap.command.hide.others")
                .handler(this::execute));
    }

    private void execute(CommandContext<CommandSender> context) {
        Player target = CommandHelper.resolvePlayer(context);
        CommandSender sender = context.getSender();

        if (PlayerManager.INSTANCE.isHidden(target)) {
            Lang.send(sender, Lang.COMMAND_HIDE_ALREADY_HIDDEN, Placeholder.unparsed("player", target.getName()));
            return;
        }
        PlayerManager.INSTANCE.setHidden(target, true, true);
        Lang.send(sender, Lang.COMMAND_HIDE_SUCCESS, Placeholder.unparsed("player", target.getName()));
    }
}
