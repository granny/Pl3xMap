package net.pl3x.map.command.commands;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.location.Location2D;
import cloud.commandframework.bukkit.parsers.location.Location2DArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.markers.Point;
import net.pl3x.map.render.job.RadiusRender;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RadiusRenderCommand extends Pl3xMapCommand {
    public RadiusRenderCommand(PaperPl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("radiusrender")
                .argument(MapWorldArgument.of("world"))
                .argument(IntegerArgument.<CommandSender>newBuilder("radius").withMin(1).withMax(100000).build())
                .argument(Location2DArgument.optional("center"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_CENTER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_RADIUSRENDER_DESCRIPTION))
                .permission("pl3xmap.command.radiusrender")
                .handler(this::execute));
    }

    public void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = resolveWorld(context);
        int radius = context.get("radius");

        Location2D loc = context.getOrDefault("center", null);
        Point center = loc == null ? Point.ZERO : Point.of(loc.getBlockX(), loc.getBlockZ());

        if (mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_RADIUSRENDER_ALREADY_RENDERING,
                    Placeholder.unparsed("world", mapWorld.getWorld().getName()));
            return;
        }

        Render render = new RadiusRender(mapWorld, sender, radius, center.getX(), center.getZ());

        if (sender instanceof Player player) {
            render.getProgress().getBossbar().show(player);
        } else {
            render.getProgress().showChat(sender);
        }

        mapWorld.startRender(render);
    }
}
