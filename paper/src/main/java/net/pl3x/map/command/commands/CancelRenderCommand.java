package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.arguments.MapWorldArgument;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.world.MapWorld;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CancelRenderCommand extends Pl3xMapCommand {
    public CancelRenderCommand(PaperPl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("cancelrender")
                .argument(MapWorldArgument.optional("world"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_WORLD_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_CANCELRENDER_DESCRIPTION))
                .permission("pl3xmap.command.cancelrender")
                .handler(this::execute));
    }

    public void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        MapWorld mapWorld = resolveWorld(context);

        if (!mapWorld.hasActiveRender()) {
            Lang.send(sender, Lang.COMMAND_CANCELRENDER_NOT_RENDERING,
                    Placeholder.unparsed("world", mapWorld.getWorld().getName()));
            return;
        }

        mapWorld.cancelRender(false);

        if (sender instanceof Player) {
            Lang.send(sender, Lang.COMMAND_CANCELRENDER_SUCCESS,
                    Placeholder.unparsed("world", mapWorld.getWorld().getName()));
        }
    }
}
