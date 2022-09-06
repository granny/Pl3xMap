package net.pl3x.map.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.PaperPl3xMap;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.BukkitAdvancedConfig;
import net.pl3x.map.configuration.BukkitConfig;
import net.pl3x.map.configuration.BukkitLang;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.CommandSender;

public class ReloadCommand extends Pl3xMapCommand {
    public ReloadCommand(PaperPl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
    }

    @Override
    public void register() {
        getCommandManager().registerSubcommand(builder -> builder.literal("reload")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_RELOAD_DESCRIPTION))
                .permission("pl3xmap.command.reload")
                .handler(this::execute));
    }

    public void execute(CommandContext<CommandSender> context) {
        CommandSender sender = context.getSender();

        getPlugin().disable();

        BukkitConfig.reload();
        BukkitLang.reload();
        BukkitAdvancedConfig.reload();

        getPlugin().enable();

        String version = getPlugin().getDescription().getVersion();

        Lang.send(sender, Lang.COMMAND_RELOAD_SUCCESS,
                Placeholder.unparsed("version", version));
    }
}
