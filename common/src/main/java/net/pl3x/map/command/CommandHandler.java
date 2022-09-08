package net.pl3x.map.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.CommandManager;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import java.util.function.UnaryOperator;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface CommandHandler {
    @NonNull
    CommandManager<Sender> getManager();

    @NonNull
    CommandManager<Sender> command(Command.@NonNull Builder<Sender> load);

    void registerSubcommand(@NonNull UnaryOperator<Command.Builder<Sender>> builder);

    @NonNull
    CommandHelpHandler<Sender> createHelpCommand();

    @NonNull
    AudienceProvider<Sender> getAudience();
}
