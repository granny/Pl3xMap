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

import cloud.commandframework.arguments.standard.IntegerArgument;
import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.PointArgument;
import net.pl3x.map.core.command.argument.WorldArgument;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

public class RadiusRenderCommand extends Pl3xMapCommand {
    public RadiusRenderCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("radiusrender")
                .argument(WorldArgument.of("world"), description(Lang.COMMAND_ARGUMENT_REQUIRED_WORLD_DESCRIPTION))
                .argument(IntegerArgument.<Sender>builder("radius").withMin(1).withMax(1000000).build())
                .argument(PointArgument.optional("center"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_CENTER_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_RADIUSRENDER_DESCRIPTION))
                .permission("pl3xmap.command.radiusrender")
                .handler(this::execute));
    }

    public void execute(@NonNull CommandContext<@NonNull Sender> context) {
        CompletableFuture.runAsync(() -> executeAsync(context));
    }

    private void executeAsync(@NonNull CommandContext<@NonNull Sender> context) {
        Sender sender = context.getSender();
        World world = context.get("world");
        int radius = context.get("radius");
        Point center = PointArgument.resolve(context, "center");

        int rX = center.x() >> 9;
        int rZ = center.z() >> 9;
        int rR = radius >> 9;

        int minX = rX - rR;
        int minZ = rZ - rR;
        int maxX = rX + rR;
        int maxZ = rZ + rR;

        Collection<Point> regions = world.listRegions(true);

        regions.removeIf(region -> region.x() < minX || region.z() < minZ || region.x() > maxX || region.z() > maxZ);

        if (Config.DEBUG_MODE) {
            regions.forEach(region -> Logger.debug("Adding region: " + region));
        }

        Pl3xMap.api().getRegionProcessor().addRegions(world, regions);

        sender.sendMessage(Lang.COMMAND_RADIUSRENDER_STARTING);
    }
}
