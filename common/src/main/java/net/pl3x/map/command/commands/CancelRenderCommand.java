package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.WorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.player.Player;
import net.pl3x.map.world.World;

public class CancelRenderCommand extends Pl3xMapCommand {
    public CancelRenderCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("cancelrender")
                .argument(WorldArgument.optional(WorldArgument.WORLD), description(Lang.COMMAND_ARGUMENT_OPTIONAL_WORLD_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_CANCELRENDER_DESCRIPTION))
                .permission("pl3xmap.command.cancelrender")
                .handler(this::execute));
    }

    public void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        World world = resolveWorld(context);

        if (!world.hasActiveRender()) {
            sender.send(Lang.COMMAND_CANCELRENDER_NOT_RENDERING,
                    Placeholder.unparsed(WorldArgument.WORLD, world.getName()));
            return;
        }

        world.cancelRender(false);

        if (sender instanceof Player) {
            sender.send(Lang.COMMAND_CANCELRENDER_SUCCESS,
                    Placeholder.unparsed(WorldArgument.WORLD, world.getName()));
        }
    }
}
