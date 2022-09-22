package net.pl3x.map.command.commands;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.command.argument.PointArgument;
import net.pl3x.map.command.argument.WorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.markers.Point;
import net.pl3x.map.player.Player;
import net.pl3x.map.render.job.RadiusRender;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.world.World;

public class RadiusRenderCommand extends Pl3xMapCommand {
    public RadiusRenderCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("radiusrender")
                .argument(WorldArgument.of("world"))
                .argument(IntegerArgument.<Sender>newBuilder("radius").withMin(1).withMax(100000).build())
                .argument(PointArgument.optional("center"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_CENTER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_RADIUSRENDER_DESCRIPTION))
                .permission("pl3xmap.command.radiusrender")
                .handler(this::execute));
    }

    public void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        World world = WorldArgument.resolve(context, "world");
        int radius = context.get("radius");

        if (world.hasActiveRender()) {
            sender.send(Lang.COMMAND_RADIUSRENDER_ALREADY_RENDERING,
                    Placeholder.unparsed("world", world.getName()));
            return;
        }

        Point center = context.getOrDefault("center", null);
        if (center == null) {
            if (sender instanceof Player player) {
                center = player.getPosition();
            } else {
                sender.send(Lang.ERROR_MUST_SPECIFY_CENTER);
                return;
            }
        }

        Render render = new RadiusRender(world, sender, radius, center.getX(), center.getZ());

        if (sender instanceof Player player) {
            render.getProgress().getBossbar().show(player);
        } else {
            render.getProgress().showChat(sender);
        }

        world.startRender(render);
    }
}
