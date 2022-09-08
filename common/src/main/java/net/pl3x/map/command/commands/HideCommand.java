package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.PlayerArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.Player;

public class HideCommand extends Pl3xMapCommand {
    public HideCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("hide")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_HIDE_DESCRIPTION))
                .permission("pl3xmap.command.hide")
                .handler(this::execute));
        getHandler().registerSubcommand(builder -> builder.literal("hide")
                .argument(PlayerArgument.optional(PlayerArgument.PLAYER), description(Lang.COMMAND_ARGUMENT_OPTIONAL_PLAYER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_HIDE_DESCRIPTION))
                .permission("pl3xmap.command.hide.others")
                .handler(this::execute));
    }

    private void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        Player player = resolvePlayer(context);

        if (player.isHidden()) {
            sender.send(Lang.COMMAND_HIDE_ALREADY_HIDDEN,
                    Placeholder.unparsed(PlayerArgument.PLAYER, player.getName()));
            return;
        }
        player.setHidden(true, true);
        sender.send(Lang.COMMAND_HIDE_SUCCESS,
                Placeholder.unparsed(PlayerArgument.PLAYER, player.getName()));
    }
}
