package net.pl3x.map.forge.command;

import cloud.commandframework.Command;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.forge.ForgeServerCommandManager;
import io.leangen.geantyref.TypeToken;
import net.minecraft.commands.arguments.DimensionArgument;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.parser.WorldParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeCommandManager implements CommandHandler {
    private final ForgeServerCommandManager<@NonNull Sender> manager;
    private final Command.Builder<@NonNull Sender> root;

    public ForgeCommandManager() {
        this.manager = new ForgeServerCommandManager<>(CommandExecutionCoordinator.simpleCoordinator(), ForgeSender::create, Sender::getSender);

        var brigadier = getManager().brigadierManager();
        brigadier.setNativeNumberSuggestions(false);
        brigadier.registerMapping(new TypeToken<WorldParser<Sender>>() {
        }, builder -> builder.toConstant(DimensionArgument.dimension()).cloudSuggestions());

        this.root = buildRoot();
        getManager().command(getRoot());
        registerSubcommands();
    }

    @Override
    public @NonNull ForgeServerCommandManager<@NonNull Sender> getManager() {
        return this.manager;
    }

    @Override
    public Command.@NonNull Builder<@NonNull Sender> getRoot() {
        return this.root;
    }
}
