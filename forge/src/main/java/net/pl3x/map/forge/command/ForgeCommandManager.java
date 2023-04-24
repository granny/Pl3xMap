package net.pl3x.map.forge.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.forge.ForgeServerCommandManager;
import cloud.commandframework.meta.CommandMeta;
import com.google.common.collect.ImmutableList;
import io.leangen.geantyref.TypeToken;
import net.minecraft.commands.arguments.DimensionArgument;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.WorldArgument;
import net.pl3x.map.core.command.argument.parser.WorldParser;
import net.pl3x.map.core.command.commands.ReloadCommand;
import net.pl3x.map.core.command.commands.ResetMapCommand;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ForgeCommandManager implements CommandHandler {
    private final ForgeServerCommandManager<@NonNull Sender> manager;
    private final Command.Builder<@NonNull Sender> root;

    public ForgeCommandManager() {
        this.manager = new ForgeServerCommandManager<>(CommandExecutionCoordinator.simpleCoordinator(), ForgeSender::create, Sender::getSender);

        var brigadier = this.manager.brigadierManager();
        brigadier.setNativeNumberSuggestions(false);
        brigadier.registerMapping(new TypeToken<WorldParser<Sender>>() {
        }, builder -> builder.toConstant(DimensionArgument.dimension()).cloudSuggestions());

        this.root = this.manager.commandBuilder("map", "pl3xmap")
                .permission("pl3xmap.command.map")
                .meta(CommandMeta.DESCRIPTION, "Pl3xMap command. '/map help'")
                .handler(context -> context.getSender().sendMessage(Lang.COMMAND_BASE));

        this.manager.command(this.root);

        ImmutableList.of(
                new ReloadCommand(this),
                new ResetMapCommand(this)
        ).forEach(Pl3xMapCommand::register);
    }

    @Override
    public @NonNull CommandManager<@NonNull Sender> getManager() {
        return this.manager;
    }

    public Command.@NonNull Builder<@NonNull Sender> getRoot() {
        return this.root;
    }
}
