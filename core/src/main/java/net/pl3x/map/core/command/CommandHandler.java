package net.pl3x.map.core.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.meta.CommandMeta;
import java.util.List;
import java.util.function.UnaryOperator;
import net.pl3x.map.core.command.commands.ConfirmCommand;
import net.pl3x.map.core.command.commands.ReloadCommand;
import net.pl3x.map.core.command.commands.ResetMapCommand;
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
                new ReloadCommand(this),
                new ResetMapCommand(this)
        ).forEach(Pl3xMapCommand::register);
    }
}
