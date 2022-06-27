package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.render.FullRender;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class FullRenderCommand extends Pl3xMapCommand {
    public FullRenderCommand(Pl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("fullrender")
                .argument(MapWorldArgument.optional("world"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_WORLD_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_FULLRENDER_DESCRIPTION))
                .permission("pl3xmap.command.fullrender")
                .handler(this::execute));
    }

    public void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = resolveWorld(context);

        if (mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_FULLRENDER_ALREADY_RENDERING,
                    Placeholder.unparsed("world", mapWorld.getName()));
            return;
        }

        FullRender render = new FullRender(mapWorld, sender);

        if (sender instanceof Player player) {
            render.getProgress().getBossbar().show(player);
        } else {
            render.getProgress().showChat(sender);
        }

        mapWorld.startRender(render);
    }
}
