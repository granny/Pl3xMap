package net.pl3x.map.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.CommandSender;

public class AddonCommand extends Pl3xMapCommand {
    public AddonCommand(PaperPl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> {
            Command.Builder<CommandSender> addon = builder.literal("addons")
                    .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_ADDON_DESCRIPTION))
                    .permission("pl3xmap.command.addons")
                    .handler(this::executeList);
            getCommandManager().command(addon.literal("load")
                    .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_ADDON_DESCRIPTION))
                    .permission("pl3xmap.command.addons.load")
                    .handler(this::executeLoad));
            getCommandManager().command(addon.literal("unload")
                    .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_ADDON_DESCRIPTION))
                    .permission("pl3xmap.command.addons.unload")
                    .handler(this::executeUnload));
            return addon;
        });
    }

    public void executeList(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Lang.send(sender, "list..");
    }

    public void executeLoad(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Lang.send(sender, "load..");
    }

    public void executeUnload(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();
        Lang.send(sender, "unload..");
    }
}
