package net.pl3x.map.bukkit.command;

import cloud.commandframework.Command;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Sender;
import org.bukkit.plugin.Plugin;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BukkitCommandManager implements CommandHandler {
    private final PaperCommandManager<@NonNull Sender> manager;
    private final Command.Builder<@NonNull Sender> root;

    public BukkitCommandManager(@NonNull Plugin plugin) throws Exception {
        this.manager = new PaperCommandManager<>(plugin, CommandExecutionCoordinator.simpleCoordinator(), BukkitSender::create, Sender::getSender);

        getManager().registerBrigadier();
        CloudBrigadierManager<Sender, ?> brigadier = getManager().brigadierManager();
        if (brigadier != null) {
            brigadier.setNativeNumberSuggestions(false);
        }

        if (getManager().hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            getManager().registerAsynchronousCompletions();
        }

        this.root = buildRoot();
        getManager().command(getRoot());
        registerSubcommands();
    }

    @Override
    public @NonNull PaperCommandManager<@NonNull Sender> getManager() {
        return this.manager;
    }

    @Override
    public Command.@NonNull Builder<@NonNull Sender> getRoot() {
        return this.root;
    }
}
