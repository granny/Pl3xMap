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

public class ShowCommand extends Pl3xMapCommand {
    public ShowCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("show")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_SHOW_DESCRIPTION))
                .permission("pl3xmap.command.show")
                .handler(this::execute));
        getHandler().registerSubcommand(builder -> builder.literal("show")
                .argument(PlayerArgument.optional("player"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_PLAYER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_SHOW_DESCRIPTION))
                .permission("pl3xmap.command.show.others")
                .handler(this::execute));
    }

    private void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        Player target = PlayerArgument.resolve(context, "player");

        if (!target.isHidden()) {
            sender.send(Lang.COMMAND_SHOW_NOT_HIDDEN,
                    Placeholder.unparsed("player", target.getName()));
            return;
        }
        target.setHidden(false, true);
        sender.send(Lang.COMMAND_SHOW_SUCCESS,
                Placeholder.unparsed("player", target.getName()));
    }
}
