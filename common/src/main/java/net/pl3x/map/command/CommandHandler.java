package net.pl3x.map.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import java.util.function.UnaryOperator;
import net.kyori.adventure.audience.Audience;
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
    @NonNull
    CommandManager<Sender> getManager();

    /**
     * Register a new command.
     *
     * @param builder command builder
     * @return the command manager
     */
    @NonNull
    CommandManager<Sender> command(Command.@NonNull Builder<Sender> builder);

    /**
     * Register a new subcommand.
     *
     * @param builder command builder
     */
    void registerSubcommand(@NonNull UnaryOperator<Command.Builder<Sender>> builder);

    /**
     * Creates a new command help handler instance.
     * <p>
     * The command helper handler can be used to assist in the production of command help menus, etc.
     * <p>
     * This command help handler instance will display all commands registered in this command manager.
     *
     * @return a new command helper handler instance
     */
    @NonNull
    CommandHelpHandler<Sender> createHelpCommand();

    /**
     * Get an audience provider for sender types which are already an {@link Audience}.
     *
     * @return native audience provider
     */
    @NonNull
    AudienceProvider<Sender> getAudience();
}
