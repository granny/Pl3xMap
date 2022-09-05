package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;

public class PauseRenderCommand extends Pl3xMapCommand {
    public PauseRenderCommand(Pl3xMapPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("pauserender")
                .argument(MapWorldArgument.optional("world"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_WORLD_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_PAUSERENDER_DESCRIPTION))
                .permission("pl3xmap.command.pauserender")
                .handler(this::execute));
    }

    public void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld world = resolveWorld(context);

        world.setPaused(!world.isPaused());

        if (world.isPaused()) {
            Lang.send(sender, Lang.COMMAND_PAUSERENDER_PAUSED,
                    Placeholder.unparsed("world", world.getName()));
        } else {
            Lang.send(sender, Lang.COMMAND_PAUSERENDER_RESUMED,
                    Placeholder.unparsed("world", world.getName()));
        }
    }
}
