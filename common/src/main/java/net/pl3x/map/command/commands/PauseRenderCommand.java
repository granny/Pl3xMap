package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.WorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.World;

public class PauseRenderCommand extends Pl3xMapCommand {
    public PauseRenderCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("pauserender")
                .argument(WorldArgument.optional(WorldArgument.WORLD), description(Lang.COMMAND_ARGUMENT_OPTIONAL_WORLD_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_PAUSERENDER_DESCRIPTION))
                .permission("pl3xmap.command.pauserender")
                .handler(this::execute));
    }

    public void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        World world = resolveWorld(context);

        world.setPaused(!world.isPaused());

        if (world.isPaused()) {
            sender.send(Lang.COMMAND_PAUSERENDER_PAUSED,
                    Placeholder.unparsed(WorldArgument.WORLD, world.getName()));
        } else {
            sender.send(Lang.COMMAND_PAUSERENDER_RESUMED,
                    Placeholder.unparsed(WorldArgument.WORLD, world.getName()));
        }
    }
}
