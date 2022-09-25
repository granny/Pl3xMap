package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.io.IOException;
import java.nio.file.Path;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.WorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;

public class ResetMapCommand extends Pl3xMapCommand {
    public ResetMapCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("resetmap")
                .argument(WorldArgument.of("world"))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_RESETMAP_DESCRIPTION))
                .meta(CommandConfirmationManager.META_CONFIRMATION_REQUIRED, true)
                .permission("pl3xmap.command.resetmap")
                .handler(this::execute));
    }

    private void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        World world = WorldArgument.resolve(context, "world");

        // check for active renders first
        if (world.hasActiveRender()) {
            sender.send(Lang.COMMAND_RESETMAP_ACTIVE_RENDER,
                    Placeholder.unparsed("world", world.getName()));
            return;
        }

        // pause background render
        world.setPaused(true);

        // delete all tiles for world
        Path worldTilesDir = world.getTilesDir();
        try {
            FileUtil.deleteSubdirectories(worldTilesDir);
        } catch (IOException e) {
            // resume background render
            world.setPaused(false);
            throw new IllegalStateException(Lang.COMMAND_RESETMAP_FAILED
                    .replace("<world>", world.getName()),
                    e);
        }

        // rebuild biomes registry
        world.rebuildBiomesPaletteRegistry();

        // reset background render
        world.stopBackgroundRender();
        world.startBackgroundRender();

        // resume background render
        world.setPaused(false);

        sender.send(Lang.COMMAND_RESETMAP_SUCCESS,
                Placeholder.unparsed("world", world.getName()));
    }
}
