package net.pl3x.map.command.commands;

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.bukkit.parsers.location.Location2D;
import cloud.commandframework.bukkit.parsers.location.Location2DArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.render.AbstractRender;
import net.pl3x.map.render.RadiusRender;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RadiusRenderCommand extends Pl3xMapCommand {
    public RadiusRenderCommand(Pl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("radiusrender")
                .argument(MapWorldArgument.of("world"))
                .argument(IntegerArgument.<CommandSender>newBuilder("radius").withMin(1).withMax(100000).build())
                .argument(Location2DArgument.optional("center"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_CENTER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_RADIUSRENDER_DESCRIPTION))
                .permission("pl3xmap.command.radiusrender")
                .handler(this::execute));
    }

    public void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = resolveWorld(context);
        int radius = context.get("radius");

        Location2D center = context.getOrDefault("center", null);
        if (center == null) {
            center = Location2D.from(mapWorld.getWorld(), 0, 0);
        }

        if (mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_RADIUSRENDER_ALREADY_RENDERING,
                    Placeholder.unparsed("world", mapWorld.getName()));
            return;
        }

        AbstractRender render = new RadiusRender(mapWorld, sender, radius, center.getBlockX(), center.getBlockZ());

        if (sender instanceof Player player) {
            render.getProgress().getBossbar().show(player);
        } else {
            render.getProgress().showChat(sender);
        }

        mapWorld.startRender(render);
    }
}
