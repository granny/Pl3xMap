package net.pl3x.map.command.commands;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.progress.Progress;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class StatusCommand extends Pl3xMapCommand {
    public StatusCommand(Pl3xMapPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("status")
                .argument(MapWorldArgument.of("world"))
                .argument(StringArgument.<CommandSender>newBuilder("type").withSuggestionsProvider(this::suggestType).asOptional().build())
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_STATUS_DESCRIPTION))
                .permission("pl3xmap.command.status")
                .handler(this::execute));
    }

    private List<String> suggestType(CommandContext<CommandSender> context, String arg) {
        if (arg != null) {
            // only players can use bossbar
            return (context.getSender() instanceof Player ? Stream.of("chat", "bossbar") : Stream.of("chat"))
                    .filter(s -> s.startsWith(arg.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = resolveWorld(context);
        String type = context.getOrDefault("type", null);

        Render render = mapWorld.getActiveRender();
        Progress progress = render != null ? render.getProgress() : null;

        // toggle it
        if (type != null) {
            // if we're not actively rendering anything there is nothing to show
            if (progress == null) {
                Lang.send(sender, Lang.COMMAND_STATUS_NOT_RENDERING,
                        Placeholder.unparsed("world", mapWorld.getName())
                );
                return;
            }

            String arg = type.toLowerCase(Locale.ROOT);
            if (arg.equals("chat")) {
                if (!progress.hideChat(sender)) {
                    progress.showChat(sender);
                }
                return;
            } else if (arg.equals("bossbar")) {
                // only players can use bossbars (obviously)
                if (!(sender instanceof Player player)) {
                    Lang.send(sender, Lang.COMMAND_STATUS_PLAYER_ONLY_FEATURE);
                    return;
                }
                if (!progress.getBossbar().hide(player)) {
                    progress.getBossbar().show(player);
                }
                return;
            }
        }

        // no toggle? fine, show current status
        Lang.send(sender, Lang.COMMAND_STATUS_RENDER,
                Placeholder.unparsed("world", mapWorld.getName()),
                Placeholder.parsed("background", mapWorld.isPaused() ? "<red>Paused" : "<green>Running"),
                Placeholder.parsed("foreground", mapWorld.hasActiveRender() ? (mapWorld.isPaused() ? "<red>Paused" : "<green>Running") : "<red>Not Running")
        );

        if (progress != null && !mapWorld.isPaused()) {
            Lang.send(sender, Lang.COMMAND_STATUS_RENDER_DETAILS,
                    Placeholder.unparsed("chunks_done", Long.toString(progress.getProcessedChunks().get())),
                    Placeholder.unparsed("chunks_total", Long.toString(progress.getTotalChunks())),
                    Placeholder.unparsed("percent", String.format("%.2f", progress.getPercent())),
                    Placeholder.unparsed("remaining", "1:23:45"),
                    Placeholder.unparsed("cps", String.format("%.2f", progress.getCPS()))
            );
        }
    }
}
