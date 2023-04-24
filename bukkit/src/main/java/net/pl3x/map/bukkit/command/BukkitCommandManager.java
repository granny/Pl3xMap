package net.pl3x.map.bukkit.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandManager;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.commands.ReloadCommand;
import net.pl3x.map.core.command.commands.ResetMapCommand;
import net.pl3x.map.core.configuration.Lang;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitCommandManager implements CommandHandler {
    private final PaperCommandManager<@NonNull Sender> manager;
    private final Command.Builder<@NonNull Sender> root;

    public BukkitCommandManager(@NonNull Plugin plugin) throws Exception {
        this.manager = new PaperCommandManager<>(plugin, CommandExecutionCoordinator.simpleCoordinator(), BukkitSender::create, Sender::getSender);

        if (this.manager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            this.manager.registerBrigadier();
            CloudBrigadierManager<Sender, ?> brigadier = this.manager.brigadierManager();
            if (brigadier != null) {
                brigadier.setNativeNumberSuggestions(false);
            }
        }

        if (this.manager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            this.manager.registerAsynchronousCompletions();
        }

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

    @Override
    public Command.@NonNull Builder<@NonNull Sender> getRoot() {
        return this.root;
    }
}
