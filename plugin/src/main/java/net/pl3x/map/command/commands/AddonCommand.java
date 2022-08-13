package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.CommandSender;

public class AddonCommand extends Pl3xMapCommand {
    public AddonCommand(Pl3xMapPlugin plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("addon")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_ADDON_DESCRIPTION))
                .permission("pl3xmap.command.addon")
                .handler(this::execute));
    }

    public void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        Lang.send(sender, "todo..");
    }
}
