package net.pl3x.map.core.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import java.util.List;
import java.util.function.UnaryOperator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.pl3x.map.core.command.commands.ConfirmCommand;
import net.pl3x.map.core.command.commands.HelpCommand;
import net.pl3x.map.core.command.commands.HideCommand;
import net.pl3x.map.core.command.commands.ReloadCommand;
import net.pl3x.map.core.command.commands.ResetMapCommand;
import net.pl3x.map.core.command.commands.ShowCommand;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Represents the command handler.
 */
public interface CommandHandler {
    /**
     * Get the command manager.
     *
     * @return command manager
     */
    @NonNull CommandManager<@NonNull Sender> getManager();

    /**
     * Get the root command.
     *
     * @return the root command
     */
    Command.@NonNull Builder<@NonNull Sender> getRoot();

    default void setupExceptionHandlers() {
        new MinecraftExceptionHandler<Sender>()
                .withDefaultHandlers()
                .withDecorator(component -> Component.text()
                        .append(Lang.parse(Lang.PREFIX_COMMAND)
                                .hoverEvent(Lang.parse(Lang.CLICK_FOR_HELP))
                                .clickEvent(ClickEvent.runCommand("/map help")))
                        .append(component)
                        .build())
                .apply(getManager(), AudienceProvider.nativeAudience());
    }

    /**
     * Register a new subcommand.
     *
     * @param builder command builder
     */
    default void registerSubcommand(@NonNull UnaryOperator<Command.@NonNull Builder<@NonNull Sender>> builder) {
        this.getManager().command(builder.apply(getRoot()));
    }

    default Command.@NonNull Builder<@NonNull Sender> buildRoot() {
        return getManager().commandBuilder("map", "pl3xmap")
                .permission("pl3xmap.command.map")
                .meta(CommandMeta.DESCRIPTION, "Pl3xMap command. '/map help'")
                .handler(context -> context.getSender().sendMessage(Lang.COMMAND_BASE));
    }

    default void registerSubcommands() {
        List.of(
                new ConfirmCommand(this),
                new HelpCommand(this),
                new HideCommand(this),
                new ReloadCommand(this),
                new ResetMapCommand(this),
                new ShowCommand(this)
        ).forEach(Pl3xMapCommand::register);
    }
}
