package net.pl3x.map.core.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.PlayerArgument;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HideCommand extends Pl3xMapCommand {
    public HideCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("hide")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_HIDE_DESCRIPTION))
                .permission("pl3xmap.command.hide")
                .handler(this::execute));
        getHandler().registerSubcommand(builder -> builder.literal("hide")
                .argument(PlayerArgument.optional("player"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_PLAYER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_HIDE_DESCRIPTION))
                .permission("pl3xmap.command.hide.others")
                .handler(this::execute));
    }

    private void execute(@NonNull CommandContext<@NonNull Sender> context) {
        Sender sender = context.getSender();
        Player player = PlayerArgument.resolve(context, "player");

        if (player.isHidden()) {
            sender.sendMessage(Lang.COMMAND_HIDE_ALREADY_HIDDEN,
                    Placeholder.unparsed("player", player.getName()));
            return;
        }

        player.setHidden(true, true);

        sender.sendMessage(Lang.COMMAND_HIDE_SUCCESS,
                Placeholder.unparsed("player", player.getName()));
    }
}
