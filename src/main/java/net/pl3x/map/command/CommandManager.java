package net.pl3x.map.command;

import cloud.commandframework.Command;
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
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.Pl3xMap;
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
import net.pl3x.map.command.exception.CompletedSuccessfullyException;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.CommandSender;

public class CommandManager extends PaperCommandManager<CommandSender> {
    public CommandManager(Pl3xMap plugin) throws Exception {
        super(plugin, CommandExecutionCoordinator.simpleCoordinator(), UnaryOperator.identity(), UnaryOperator.identity());

        if (hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)) {
            registerBrigadier();
            CloudBrigadierManager<?, ?> brigManager = brigadierManager();
            if (brigManager != null) {
                brigManager.setNativeNumberSuggestions(false);
            }
        }

        if (hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            registerAsynchronousCompletions();
        }

        registerExceptionHandlers();

        ImmutableList.of(
                new CancelRenderCommand(plugin, this),
                new ConfirmCommand(plugin, this),
                new FullRenderCommand(plugin, this),
                new HelpCommand(plugin, this),
                new HideCommand(plugin, this),
                new PauseRenderCommand(plugin, this),
                new RadiusRenderCommand(plugin, this),
                new ReloadCommand(plugin, this),
                new ResetMapCommand(plugin, this),
                new ShowCommand(plugin, this),
                new StatusCommand(plugin, this)
        ).forEach(Pl3xMapCommand::register);
    }

    private void registerExceptionHandlers() {
        new MinecraftExceptionHandler<CommandSender>()
                .withDefaultHandlers()
                .withDecorator(component -> Component.text()
                        .append(MiniMessage.miniMessage().deserialize(Lang.PREFIX_COMMAND)
                                .hoverEvent(MiniMessage.miniMessage().deserialize(Lang.CLICK_FOR_HELP))
                                .clickEvent(ClickEvent.runCommand("/map help")))
                        .append(component)
                        .build())
                .apply(this, AudienceProvider.nativeAudience());
        var handler = Objects.requireNonNull(getExceptionHandler(CommandExecutionException.class));
        registerExceptionHandler(CommandExecutionException.class, (sender, exception) -> {
            Throwable cause = exception.getCause();
            if (cause instanceof CompletedSuccessfullyException) {
                return;
            }
            handler.accept(sender, exception);
        });
    }

    public void registerSubcommand(UnaryOperator<Command.Builder<CommandSender>> builderModifier) {
        command(builderModifier.apply(rootBuilder()));
    }

    private Command.Builder<CommandSender> rootBuilder() {
        return commandBuilder("map", "pl3xmap")
                .permission("pl3xmap.command.map")
                /* MinecraftHelp uses the MinecraftExtrasMetaKeys.DESCRIPTION meta,
                 * this is just so we give Bukkit a description for our commands
                 * in the Bukkit and EssentialsX '/help' command */
                .meta(CommandMeta.DESCRIPTION, "Pl3xMap command. '/map help'");
    }
}
