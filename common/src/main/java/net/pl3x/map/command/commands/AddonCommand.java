package net.pl3x.map.command.commands;

import cloud.commandframework.Command;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.util.Comparator;
import java.util.List;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.addon.Addon;
import net.pl3x.map.addon.AddonInfo;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.configuration.Lang;
import org.jetbrains.annotations.NotNull;

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
        context.getSender().send(getAddonList());
    }

    public void executeLoad(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        sender.send("load..");
    }

    public void executeUnload(CommandContext<Sender> context) {
        Sender sender = context.getSender();
        sender.send("unload..");
    }

    @NotNull
    private Component getAddonList() {
        List<Addon> addons = Pl3xMap.api().getAddonRegistry().getAddons().stream()
                .sorted(Comparator.comparing(addon -> addon.getName().toLowerCase())).toList();

        TextComponent.Builder builder = Component.text()
                .append(Lang.parse("Addons (<count>): ", Placeholder.unparsed("count", Integer.toString(addons.size()))));

        int index = 0;
        for (Addon addon : addons) {
            if (index++ > 0) {
                builder.append(Lang.parse(", "));
            }

            AddonInfo info = addon.getInfo();

            TextComponent.Builder hover = Component.text()
                    .append(Lang.parse("Version: <green><version>",
                            Placeholder.unparsed("version", info.getVersion())));

            if (info.getDescription() != null) {
                hover.append(Lang.parse("\nDescription: <green><desc>",
                        Placeholder.unparsed("desc", info.getDescription())));
            }
            //if (info.getWebsite() != null) {
            //    hover.append(Lang.parse("\nWebsite: <green><url>",
            //            Placeholder.unparsed("url", info.getWebsite())));
            //}
            if (info.getAuthor() != null) {
                hover.append(Lang.parse("\nAuthor: <green><author>",
                        Placeholder.unparsed("author", info.getAuthor())));
            }

            builder.append(Lang.parse((true/*addon.isEnabled()*/ ? "<green>" : "<red>") + info.getName())
                    .hoverEvent(hover.build()));
        }

        return builder.build();
    }
}
