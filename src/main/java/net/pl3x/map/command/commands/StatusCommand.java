package net.pl3x.map.command.commands;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.render.progress.Progress;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StatusCommand extends Pl3xMapCommand {
    public StatusCommand(Pl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("status")
                .argument(MapWorldArgument.of("world"))
                .argument(StringArgument.<CommandSender>newBuilder("type").withSuggestionsProvider(this::suggest).asOptional().build())
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_STATUS_DESCRIPTION))
                .permission("pl3xmap.command.status")
                .handler(this::execute));
    }

    private List<String> suggest(CommandContext<CommandSender> context, String arg) {
        if (arg != null) {
            return Stream.of("chat", "bossbar")
                    .filter(s -> s.startsWith(arg.toLowerCase(Locale.ROOT)))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    private void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = resolveWorld(context);
        String type = context.getOrDefault("type", null);

        // if we're not actively rendering anything, background is working
        if (!mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_STATUS_NOT_RENDERING,
                    Placeholder.unparsed("world", mapWorld.getName())
            );
            return;
        }

        AbstractRender render = mapWorld.getActiveRender();
        Progress progress = render.getProgress();

        // toggle it
        if (type != null) {
            String arg = type.toLowerCase(Locale.ROOT);
            if (arg.equals("chat")) {
                if (!progress.hide(sender)) {
                    progress.show(sender);
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
        Lang.send(sender, Lang.COMMAND_STATUS_RENDERING,
                Placeholder.unparsed("world", mapWorld.getName()),
                Placeholder.unparsed("type", render.getType()),
                Placeholder.parsed("status", "<green>Running"),
                Placeholder.unparsed("chunks_done", Long.toString(progress.getProcessedChunks().get())),
                Placeholder.unparsed("chunks_total", Long.toString(progress.getTotalChunks())),
                Placeholder.unparsed("percent", String.format("%.2f", progress.getPercent())),
                Placeholder.unparsed("remaining", "1:23:45"),
                Placeholder.unparsed("cps", String.format("<gold>%.2f", progress.getCPS()))
        );
    }
}
