package net.pl3x.map.core.command.commands;

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ReloadCommand extends Pl3xMapCommand {
    public ReloadCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("reload")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_RELOAD_DESCRIPTION))
                .permission("pl3xmap.command.reload")
                .handler(this::execute));
    }

    public void execute(@NonNull CommandContext<@NonNull Sender> context) {
        // disable everything
        Pl3xMap.api().disable();

        // enable everything
        Pl3xMap.api().enable();

        // notify sender
        context.getSender().sendMessage(Lang.COMMAND_RELOAD_SUCCESS,
                Placeholder.unparsed("version", Pl3xMap.api().getVersion()));
    }
}
