package net.pl3x.map.command.commands;

import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.CommandSender;

import java.util.concurrent.TimeUnit;

import static net.kyori.adventure.text.Component.text;

public final class ConfirmCommand extends Pl3xMapCommand {
    private final CommandConfirmationManager<CommandSender> confirmationManager = new CommandConfirmationManager<>(
            15L,
            TimeUnit.SECONDS,
            context -> context.getCommandContext().getSender().sendMessage(confirmationRequiredMessage()),
            sender -> Lang.send(sender, Lang.COMMAND_CONFIRM_NO_PENDING_MESSAGE)
    );

    private static ComponentLike confirmationRequiredMessage() {
        return text().append(Lang.parse(Lang.COMMAND_CONFIRM_CONFIRMATION_REQUIRED_MESSAGE))
                .hoverEvent(Lang.parse(Lang.CLICK_TO_CONFIRM))
                .clickEvent(ClickEvent.runCommand("/map confirm"));
    }

    public ConfirmCommand(Pl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        this.confirmationManager.registerConfirmationProcessor(getCommandManager());

        getCommandManager().registerSubcommand(builder -> builder.literal("confirm")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_CONFIRM_DESCRIPTION))
                .handler(this.confirmationManager.createConfirmationExecutionHandler()));
    }
}
