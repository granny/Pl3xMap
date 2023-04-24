package net.pl3x.map.core.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.WorldArgument;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ResetMapCommand extends Pl3xMapCommand {
    public ResetMapCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("resetmap")
                .argument(WorldArgument.of("world"))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_RESETMAP_DESCRIPTION))
                .meta(CommandConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .permission("pl3xmap.command.resetmap")
                .handler(this::execute));
    }

    private void execute(@NonNull CommandContext<@NonNull Sender> context) {
        Sender sender = context.getSender();
        World world = WorldArgument.resolve(context, "world");

        sender.sendMessage("reset map command triggered");
    }
}
