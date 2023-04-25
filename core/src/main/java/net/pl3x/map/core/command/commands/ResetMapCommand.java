package net.pl3x.map.core.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.io.IOException;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.WorldArgument;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ResetMapCommand extends Pl3xMapCommand {
    public ResetMapCommand(@NonNull CommandHandler handler) {
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

    private void execute(@NonNull CommandContext<@NonNull Sender> context) {
        Sender sender = context.getSender();
        World world = WorldArgument.resolve(context, "world");

        // unregister the world
        Pl3xMap.api().getWorldRegistry().unregister(world.getName());

        // delete world files
        String result;
        try {
            FileUtil.deleteDirectory(world.getTilesDirectory());
            result = Lang.COMMAND_RESETMAP_SUCCESS;
        } catch (IOException e) {
            result = Lang.COMMAND_RESETMAP_FAILED;
        }

        // create a new world
        Pl3xMap.api().getWorldRegistry().register(Pl3xMap.api().cloneWorld(world));

        // notify sender
        sender.sendMessage(result, Placeholder.unparsed("world", world.getName()));
    }
}
