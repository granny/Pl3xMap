/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.command.commands;

import cloud.commandframework.CommandHelpHandler.VerboseHelpEntry;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import cloud.commandframework.minecraft.extras.MinecraftHelp;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

public class HelpCommand extends Pl3xMapCommand {
    private final MinecraftHelp<@NonNull Sender> minecraftHelp;

    public HelpCommand(@NonNull CommandHandler handler) {
        super(handler);
        this.minecraftHelp = MinecraftHelp.createNative("/map help", handler.getManager());
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
        getHandler().registerSubcommand(builder -> builder.literal("help")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_HELP_DESCRIPTION))
                .argument(StringArgument.<Sender>builder("query").greedy().asOptional()
                        .withSuggestionsProvider((context, input) -> getHandler().getManager()
                                .createCommandHelpHandler().queryRootIndex(context.getSender())
                                .getEntries().stream().map(VerboseHelpEntry::getSyntaxString).toList())
                        .build(), description(Lang.COMMAND_HELP_ARGUMENT_QUERY_DESCRIPTION))
                .permission("pl3xmap.command.help")
                .handler(ctx -> {
                    String query = ctx.<String>getOptional("query").orElse("");
                    this.minecraftHelp.queryCommands(query, ctx.getSender());
                }));
    }
}
