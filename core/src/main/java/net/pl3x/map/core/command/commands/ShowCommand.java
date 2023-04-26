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

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.PlayerArgument;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.player.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ShowCommand extends Pl3xMapCommand {
    public ShowCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("show")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_SHOW_DESCRIPTION))
                .permission("pl3xmap.command.show")
                .handler(this::execute));
        getHandler().registerSubcommand(builder -> builder.literal("show")
                .argument(PlayerArgument.optional("player"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_PLAYER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_SHOW_DESCRIPTION))
                .permission("pl3xmap.command.show.others")
                .handler(this::execute));
    }

    private void execute(@NonNull CommandContext<@NonNull Sender> context) {
        Sender sender = context.getSender();
        Player target = PlayerArgument.resolve(context, "player");

        if (!target.isHidden()) {
            sender.sendMessage(Lang.COMMAND_SHOW_NOT_HIDDEN,
                    Placeholder.unparsed("player", target.getName()));
            return;
        }

        target.setHidden(false, true);

        sender.sendMessage(Lang.COMMAND_SHOW_SUCCESS,
                Placeholder.unparsed("player", target.getName()));
    }
}
