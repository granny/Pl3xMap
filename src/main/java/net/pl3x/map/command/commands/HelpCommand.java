package net.pl3x.map.command.commands;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.AudienceProvider;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.command.CommandHelper;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.Lang;
import org.bukkit.command.CommandSender;

import java.util.stream.Collectors;

public class HelpCommand extends Pl3xMapCommand {
    private final MinecraftHelp<CommandSender> minecraftHelp;

    public HelpCommand(Pl3xMap plugin, CommandManager commandManager) {
        super(plugin, commandManager);
        this.minecraftHelp = new MinecraftHelp<>("/map help", AudienceProvider.nativeAudience(), commandManager);
        this.minecraftHelp.setHelpColors(MinecraftHelp.HelpColors.of(
                TextColor.color(0x5B00FF),
                NamedTextColor.WHITE,
                TextColor.color(0xC028FF),
                NamedTextColor.GRAY,
                NamedTextColor.DARK_GRAY
        ));
        this.minecraftHelp.setMessage(MinecraftHelp.MESSAGE_HELP_TITLE, "Pl3xMap Help");
    }

    @Override
    public void register() {
        var commandHelpHandler = getCommandManager().createCommandHelpHandler();
        var helpQueryArgument = StringArgument.<CommandSender>newBuilder("query")
                .greedy()
                .asOptional()
                .withSuggestionsProvider((context, input) -> {
                    var indexHelpTopic = (CommandHelpHandler.IndexHelpTopic<CommandSender>) commandHelpHandler.queryHelp(context.getSender(), "");
                    return indexHelpTopic.getEntries()
                            .stream()
                            .map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString)
                            .collect(Collectors.toList());
                })
                .build();
        getCommandManager().registerSubcommand(builder -> builder.literal("help")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, MiniMessage.miniMessage().deserialize(Lang.COMMAND_HELP_DESCRIPTION))
                .argument(helpQueryArgument, CommandHelper.description(Lang.COMMAND_ARGUMENT_HELP_QUERY_DESCRIPTION))
                .permission("pl3xmap.command.help")
                .handler(this::execute));
    }

    private void execute(CommandContext<CommandSender> context) {
        this.minecraftHelp.queryCommands(context.<String>getOptional("query").orElse(""), context.getSender());
    }
}
