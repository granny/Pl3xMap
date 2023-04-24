package net.pl3x.map.core.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import java.util.function.UnaryOperator;
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
}
