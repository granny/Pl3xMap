package net.pl3x.map.core.command.commands;

import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfirmCommand extends Pl3xMapCommand {
    private final CommandConfirmationManager<Sender> confirmationManager = new CommandConfirmationManager<>(
            15L, TimeUnit.SECONDS,
            context -> context.getCommandContext().getSender().sendMessage(
                    Component.text().append(Lang.parse(Lang.COMMAND_CONFIRM_CONFIRMATION_REQUIRED_MESSAGE))
                            .hoverEvent(Lang.parse(Lang.CLICK_TO_CONFIRM))
                            .clickEvent(ClickEvent.runCommand("/map confirm"))
            ),
            sender -> sender.sendMessage(Lang.COMMAND_CONFIRM_NO_PENDING_MESSAGE)
    );

    public ConfirmCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        this.confirmationManager.registerConfirmationProcessor(getHandler().getManager());

        getHandler().registerSubcommand(builder -> builder.literal("confirm")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_CONFIRM_DESCRIPTION))
                .permission("pl3xmap.command.confirm")
                .handler(this.confirmationManager.createConfirmationExecutionHandler()));
    }
}
