package net.pl3x.map.command;

import cloud.commandframework.Command;
import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.CommandManager;
import cloud.commandframework.brigadier.CloudBrigadierManager;
import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.exceptions.CommandExecutionException;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.meta.CommandMeta;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExceptionHandler;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.collect.ImmutableList;
import java.util.Objects;
import java.util.function.UnaryOperator;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.command.commands.AddonCommand;
import net.pl3x.map.command.commands.CancelRenderCommand;
import net.pl3x.map.command.commands.ConfirmCommand;
import net.pl3x.map.command.commands.FullRenderCommand;
import net.pl3x.map.command.commands.HelpCommand;
import net.pl3x.map.command.commands.HideCommand;
import net.pl3x.map.command.commands.PauseRenderCommand;
import net.pl3x.map.command.commands.RadiusRenderCommand;
import net.pl3x.map.command.commands.ReloadCommand;
import net.pl3x.map.command.commands.ResetMapCommand;
import net.pl3x.map.command.commands.ShowCommand;
import net.pl3x.map.command.commands.StatusCommand;
import net.pl3x.map.command.exception.ArgumentParseException;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.BukkitSender;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.NotNull;

public class BukkitCommandManager extends PaperCommandManager<Sender> implements CommandHandler {
    private Command.Builder<Sender> root;

    public BukkitCommandManager(PaperPl3xMap plugin) throws Exception {
        super(plugin, CommandExecutionCoordinator.simpleCoordinator(), BukkitSender::getSender, BukkitSender::getSender);

        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            CloudBrigadierManager<Sender, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
        }

        if (hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            registerAsynchronousCompletions();
        }

        new MinecraftExceptionHandler<Sender>()
                .withDefaultHandlers()
                .withDecorator(component -> Component.text()
                        .append(Lang.parse(Lang.PREFIX_COMMAND)
                                .hoverEvent(Lang.parse(Lang.CLICK_FOR_HELP))
                                .clickEvent(ClickEvent.runCommand("/map help")))
                        .append(component)
                        .build())
                .apply(this, getAudience());
        var handler = Objects.requireNonNull(getExceptionHandler(CommandExecutionException.class));
        registerExceptionHandler(CommandExecutionException.class, (sender, exception) -> {
            if (exception.getCause() instanceof ArgumentParseException) {
                return;
            }
            handler.accept(sender, exception);
        });

        ImmutableList.of(
                new AddonCommand(this),
                new CancelRenderCommand(this),
                new ConfirmCommand(this),
                new FullRenderCommand(this),
                new HelpCommand(this),
                new HideCommand(this),
                new PauseRenderCommand(this),
                new RadiusRenderCommand(this),
                new ReloadCommand(this),
                new ResetMapCommand(this),
                new ShowCommand(this),
                new StatusCommand(this)
        ).forEach(Pl3xMapCommand::register);
    }

    @NotNull
    public CommandManager<Sender> getManager() {
        return this;
    }

    @Override
    @NonNull
    public CommandManager<Sender> command(Command.@NotNull Builder<Sender> builder) {
        return super.command(builder);
    }

    @Override
    public void registerSubcommand(@NonNull UnaryOperator<Command.Builder<Sender>> builder) {
        command(builder.apply(getRoot()));
    }

    @NonNull
    public CommandHelpHandler<Sender> createHelpCommand() {
        return createCommandHelpHandler();
    }

    @NotNull
    public AudienceProvider<Sender> getAudience() {
        return AudienceProvider.nativeAudience();
    }

    private Command.Builder<Sender> getRoot() {
        if (this.root == null) {
            this.root = commandBuilder("map", "pl3xmap")
                    .permission("pl3xmap.command.map")
                    /* MinecraftHelp uses the MinecraftExtrasMetaKeys.DESCRIPTION meta,
                     * this is just so we give Bukkit a description for our commands
                     * in the Bukkit and EssentialsX '/help' command */
                    .meta(CommandMeta.DESCRIPTION, "Pl3xMap command. '/map help'")
                    .handler(context -> context.getSender().send(Lang.COMMAND_BASE));
            command(this.root);
        }
        return this.root;
    }
}
