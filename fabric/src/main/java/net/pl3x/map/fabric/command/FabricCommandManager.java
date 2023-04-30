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
package net.pl3x.map.fabric.command;

import cloud.commandframework.Command;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.fabric.FabricServerCommandManager;
import io.leangen.geantyref.TypeToken;
import net.minecraft.commands.arguments.DimensionArgument;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.parser.WorldParser;
import org.checkerframework.checker.nullness.qual.NonNull;

public class FabricCommandManager implements CommandHandler {
    private final FabricServerCommandManager<@NonNull Sender> manager;
    private final Command.Builder<@NonNull Sender> root;

    public FabricCommandManager() {
        this.manager = new FabricServerCommandManager<>(CommandExecutionCoordinator.simpleCoordinator(), FabricSender::create, Sender::getSender);

        var brigadier = getManager().brigadierManager();
        brigadier.setNativeNumberSuggestions(false);
        brigadier.registerMapping(new TypeToken<WorldParser<Sender>>() {
        }, builder -> builder.toConstant(DimensionArgument.dimension()).cloudSuggestions());

        setupExceptionHandlers();

        this.root = buildRoot();
        getManager().command(getRoot());
        registerSubcommands();
    }

    @Override
    public @NonNull FabricServerCommandManager<@NonNull Sender> getManager() {
        return this.manager;
    }

    @Override
    public Command.@NonNull Builder<@NonNull Sender> getRoot() {
        return this.root;
    }
}
