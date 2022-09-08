package net.pl3x.map.command.commands;

import cloud.commandframework.CommandHelpHandler;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import java.util.stream.Collectors;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.pl3x.map.command.CommandHandler;
import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.command.Sender;
import net.pl3x.map.configuration.Lang;

public class HelpCommand extends Pl3xMapCommand {
    private final MinecraftHelp<Sender> minecraftHelp;

    public HelpCommand(CommandHandler handler) {
        super(handler);
        this.minecraftHelp = new MinecraftHelp<>("/map help", handler.getAudience(), handler.getManager());
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
        var helpQueryArgument = StringArgument.<Sender>newBuilder("query").greedy().asOptional()
                .withSuggestionsProvider((context, input) ->
                        ((CommandHelpHandler.IndexHelpTopic<Sender>) getHandler().createHelpCommand().queryHelp(context.getSender(), ""))
                                .getEntries().stream()
                                .map(CommandHelpHandler.VerboseHelpEntry::getSyntaxString)
                                .collect(Collectors.toList()))
                .build();
        getHandler().registerSubcommand(builder -> builder.literal("help")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_HELP_DESCRIPTION))
                .argument(helpQueryArgument, description(Lang.COMMAND_ARGUMENT_HELP_QUERY_DESCRIPTION))
                .permission("pl3xmap.command.help")
                .handler(this::execute));
    }

    private void execute(CommandContext<Sender> context) {
        this.minecraftHelp.queryCommands(context.<String>getOptional("query").orElse(""), context.getSender());
    }
}
