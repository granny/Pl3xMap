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
import net.pl3x.map.render.job.FullRender;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.world.World;

public class FullRenderCommand extends Pl3xMapCommand {
    public FullRenderCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("fullrender")
                .argument(WorldArgument.optional(WorldArgument.WORLD), description(Lang.COMMAND_ARGUMENT_OPTIONAL_WORLD_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_FULLRENDER_DESCRIPTION))
                .permission("pl3xmap.command.fullrender")
                .handler(this::execute));
    }

    public void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        World world = resolveWorld(context);

        if (world.hasActiveRender()) {
            sender.send(Lang.COMMAND_FULLRENDER_ALREADY_RENDERING,
                    Placeholder.unparsed(WorldArgument.WORLD, world.getName()));
            return;
        }

        Render render = new FullRender(world, sender);

        if (sender instanceof Player player) {
            render.getProgress().getBossbar().show(player);
        } else {
            render.getProgress().showChat(sender);
        }

        world.startRender(render);
    }
}
