package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;

import java.io.IOException;
import java.nio.file.Path;

public class ResetMapCommand extends Pl3xMapCommand {
    public ResetMapCommand(Pl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("resetmap")
                .argument(MapWorldArgument.of("world"))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_RESETMAP_DESCRIPTION))
                .meta(CommandConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .permission("pl3xmap.command.resetmap")
                .handler(this::execute));
    }

    private void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = resolveWorld(context);

        // check for active renders first
        if (mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_RESETMAP_ACTIVE_RENDER,
                    Placeholder.unparsed("world", mapWorld.getName()));
            return;
        }

        // pause background render
        mapWorld.setPaused(true);

        // delete all tiles for world
        Path worldTilesDir = MapWorld.TILES_DIR.resolve(mapWorld.getName());
        try {
            FileUtil.deleteSubdirectories(worldTilesDir);
        } catch (IOException e) {
            // resume background render
            mapWorld.setPaused(false);
            throw new IllegalStateException(Lang.COMMAND_RESETMAP_FAILED
                    .replace("<world>", mapWorld.getName()),
                    e);
        }

        // reset background render
        mapWorld.getBackgroundRender().reset();

        // resume background render
        mapWorld.setPaused(false);

        Lang.send(sender, Lang.COMMAND_RESETMAP_SUCCESS,
                Placeholder.unparsed("world", mapWorld.getName()));
    }
}
