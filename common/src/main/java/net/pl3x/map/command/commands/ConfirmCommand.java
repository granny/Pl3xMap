package net.pl3x.map.command.commands;

import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.configuration.Lang;

public class ConfirmCommand extends Pl3xMapCommand {
    private final CommandConfirmationManager<Sender> confirmationManager = new CommandConfirmationManager<>(
            15L,
            TimeUnit.SECONDS,
            context -> context.getCommandContext().getSender().sendMessage(confirmationRequiredMessage()),
            sender -> sender.send(Lang.COMMAND_CONFIRM_NO_PENDING_MESSAGE)
    );

    private static ComponentLike confirmationRequiredMessage() {
        return Component.text().append(Lang.parse(Lang.COMMAND_CONFIRM_CONFIRMATION_REQUIRED_MESSAGE))
                .hoverEvent(Lang.parse(Lang.CLICK_TO_CONFIRM))
                .clickEvent(ClickEvent.runCommand("/map confirm"));
    }

    public ConfirmCommand(CommandHandler handler) {
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
