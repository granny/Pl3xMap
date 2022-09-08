package net.pl3x.map.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.configuration.Lang;

public class AddonCommand extends Pl3xMapCommand {
    public AddonCommand(CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> {
            Command.Builder<Sender> addon = builder.literal("addons")
                    .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_ADDON_DESCRIPTION))
                    .permission("pl3xmap.command.addons")
                    .handler(this::execute);
            getHandler().command(addon.literal("load")
                    .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_ADDON_DESCRIPTION))
                    .permission("pl3xmap.command.addons.load")
                    .handler(this::executeLoad));
            getHandler().command(addon.literal("unload")
                    .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_ADDON_DESCRIPTION))
                    .permission("pl3xmap.command.addons.unload")
                    .handler(this::executeUnload));
            return addon;
        });
    }

    public void execute(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        sender.send("list..");
    }

    public void executeLoad(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        sender.send("load..");
    }

    public void executeUnload(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        sender.send("unload..");
    }
}
