/*
 * MIT License
 *
 * Copyright (c) 2020-2023 William Blake Galbreath
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

import cloud.commandframework.extra.confirmation.CommandConfirmationManager;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;

public class ConfirmCommand extends Pl3xMapCommand {
    private final CommandConfirmationManager<@NonNull Sender> confirmationManager = new CommandConfirmationManager<>(
            15L, TimeUnit.SECONDS,
            context -> context.getCommandContext().getSender().sendMessage(
                    Component.text().append(Lang.parse(Lang.COMMAND_CONFIRM_CONFIRMATION_REQUIRED_MESSAGE))
                            .hoverEvent(Lang.parse(Lang.CLICK_TO_CONFIRM))
                            .clickEvent(ClickEvent.runCommand("/map confirm"))
            ),
            sender -> sender.sendMessage(Lang.COMMAND_CONFIRM_NO_PENDING_MESSAGE)
    );

    public ConfirmCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        this.confirmationManager.registerConfirmationProcessor(getHandler().getManager());

        getHandler().registerSubcommand(builder -> builder.literal("confirm")
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_CONFIRM_DESCRIPTION))
                .permission("pl3xmap.command.confirm")
                .handler(this.confirmationManager.createConfirmationExecutionHandler()));
    }
}
