package net.pl3x.map.command.commands;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.WorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.Player;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.progress.Progress;
import net.pl3x.map.world.World;

public class StatusCommand extends Pl3xMapCommand {
    public StatusCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("status")
                .argument(WorldArgument.of("world"))
                .argument(StringArgument.<Sender>builder("type").withSuggestionsProvider(this::suggestType).asOptional().build())
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_STATUS_DESCRIPTION))
                .permission("pl3xmap.command.status")
                .handler(this::execute));
    }

    private List<String> suggestType(CommandContext<Sender> context, String arg) {
        if (arg != null) {
            // only players can use bossbar
            return (context.getSender() instanceof Player ? Stream.of("chat", "bossbar") : Stream.of("chat"))
                    .filter(s -> s.startsWith(arg.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        World world = WorldArgument.resolve(context, "world");
        String type = context.getOrDefault("type", null);

        Render render = world.getActiveRender();
        Progress progress = render != null ? render.getProgress() : null;

        // toggle it
        if (type != null) {
            // if we're not actively rendering anything there is nothing to show
            if (progress == null) {
                sender.send(Lang.COMMAND_STATUS_NOT_RENDERING,
                        Placeholder.unparsed("world", world.getName())
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
                    sender.send(Lang.COMMAND_STATUS_PLAYER_ONLY_FEATURE);
                    return;
                }
                if (!progress.getBossbar().hide(player)) {
                    progress.getBossbar().show(player);
                }
                return;
            }
        }

        // no toggle? fine, show current status
        sender.send(Lang.COMMAND_STATUS_RENDER,
                Placeholder.unparsed("world", world.getName()),
                Placeholder.parsed("background", getStatus(world.hasBackgroundRender(), world.isPaused())),
                Placeholder.parsed("foreground", getStatus(world.hasActiveRender(), world.isPaused()))
        );

        if (progress != null && !world.isPaused()) {
            sender.send(Lang.COMMAND_STATUS_RENDER_DETAILS,
                    Placeholder.unparsed("chunks_done", Long.toString(progress.getProcessedChunks().get())),
                    Placeholder.unparsed("chunks_total", Long.toString(progress.getTotalChunks())),
                    Placeholder.unparsed("percent", String.format("%.2f", progress.getPercent())),
                    Placeholder.unparsed("remaining", progress.getETA()),
                    Placeholder.unparsed("cps", String.format("%.2f", progress.getCPS()))
            );
        }
    }

    private String getStatus(boolean hasRender, boolean isPaused) {
        return hasRender ? (isPaused ? Lang.COMMAND_STATUS_RENDER_PAUSED : Lang.COMMAND_STATUS_RENDER_RUNNING) : Lang.COMMAND_STATUS_RENDER_NOT_RUNNING;
    }
}
