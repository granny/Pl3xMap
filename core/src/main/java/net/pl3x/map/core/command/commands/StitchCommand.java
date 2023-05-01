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

import cloud.commandframework.context.CommandContext;
import cloud.commandframework.minecraft.extras.MinecraftExtrasMetaKeys;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.pl3x.map.core.command.CommandHandler;
import net.pl3x.map.core.command.Pl3xMapCommand;
import net.pl3x.map.core.command.Sender;
import net.pl3x.map.core.command.argument.RendererArgument;
import net.pl3x.map.core.command.argument.WorldArgument;
import net.pl3x.map.core.command.argument.ZoomArgument;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.image.io.IO;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.renderer.Renderer;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import static net.pl3x.map.core.world.World.PNG_MATCHER;

public class StitchCommand extends Pl3xMapCommand {
    public StitchCommand(@NonNull CommandHandler handler) {
        super(handler);
    }

    @Override
    public void register() {
        getHandler().registerSubcommand(builder -> builder.literal("stitch")
                .argument(WorldArgument.of("world"), description(Lang.COMMAND_ARGUMENT_REQUIRED_WORLD_DESCRIPTION))
                .argument(RendererArgument.of("renderer"), description(Lang.COMMAND_ARGUMENT_REQUIRED_RENDERER_DESCRIPTION))
                .argument(ZoomArgument.optional("zoom"), description(Lang.COMMAND_ARGUMENT_OPTIONAL_ZOOM_DESCRIPTION))
                .meta(MinecraftExtrasMetaKeys.DESCRIPTION, Lang.parse(Lang.COMMAND_STITCH_DESCRIPTION))
                .permission("pl3xmap.command.stitch")
                .handler(this::execute));
    }

    private void execute(@NonNull CommandContext<@NonNull Sender> context) {
        new Thread(() -> executeAsync(context)).start();
    }

    private void executeAsync(@NonNull CommandContext<@NonNull Sender> context) {
        Sender sender = context.getSender();
        World world = context.get("world");
        Renderer.Builder renderer = context.get("renderer");
        int zoom = context.getOrDefault("zoom", 0);

        Path dir = world.getTilesDirectory().resolve(String.valueOf(zoom)).resolve(renderer.key());
        if (!Files.exists(dir)) {
            sender.sendMessage(Lang.COMMAND_STITCH_MISSING_DIRECTORY);
            return;
        }

        Map<Point, Path> pngFiles = new HashMap<>();
        try (Stream<Path> stream = Files.list(dir)) {
            stream.filter(PNG_MATCHER::matches).forEach(path -> {
                String[] split = path.getFileName().toString().split(".png")[0].split("_");
                if (split.length != 2) {
                    return;
                }
                int x, z;
                try {
                    x = Integer.parseInt(split[0]);
                    z = Integer.parseInt(split[1]);
                } catch (NumberFormatException e) {
                    return;
                }
                pngFiles.put(Point.of(x, z), path);
            });
        } catch (IOException e) {
            sender.sendMessage(Lang.COMMAND_STITCH_ERROR_READING_DIRECTORY);
            e.printStackTrace();
            return;
        }

        if (pngFiles.isEmpty()) {
            sender.sendMessage(Lang.COMMAND_STITCH_EMPTY_DIRECTORY);
            return;
        }

        int minX = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (Point point : pngFiles.keySet()) {
            if (point.x() < minX) minX = point.x();
            if (point.x() > maxX) maxX = point.x();
            if (point.z() < minZ) minZ = point.z();
            if (point.z() > maxZ) maxZ = point.z();
        }

        int sizeX = maxX - minX;
        int sizeZ = maxZ - minZ;

        sender.sendMessage(Lang.COMMAND_STITCH_STARTING,
                Placeholder.unparsed("count", String.valueOf(pngFiles.size())),
                Placeholder.unparsed("min-x", String.valueOf(minX)),
                Placeholder.unparsed("min-z", String.valueOf(minZ)),
                Placeholder.unparsed("max-x", String.valueOf(maxX)),
                Placeholder.unparsed("max-z", String.valueOf(maxZ)),
                Placeholder.unparsed("size-x", String.valueOf(sizeX)),
                Placeholder.unparsed("size-z", String.valueOf(sizeZ))
        );

        IO.Type io = IO.get(Config.WEB_TILE_FORMAT);

        BufferedImage stitched = new BufferedImage((sizeX + 1) << 9, (sizeZ + 1) << 9, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = stitched.createGraphics();

        for (Map.Entry<Point, Path> entry : pngFiles.entrySet()) {
            try {
                BufferedImage tile = io.read(entry.getValue());
                if (tile == null) {
                    continue;
                }
                Point point = entry.getKey();
                g2d.drawImage(tile, (point.x() - minX) << 9, (point.z() - minZ) << 9, null);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }

        g2d.dispose();

        dir = world.getTilesDirectory().resolve("stitched");
        if (!Files.exists(dir)) {
            try {
                Files.createDirectories(dir);
            } catch (IOException ignore) {
            }
        }
        String filename = renderer.key() + "_" + zoom + "." + io.extension();
        io.write(dir.resolve(filename), stitched);

        sender.sendMessage(Lang.COMMAND_STITCH_FINISHED,
                Placeholder.unparsed("count", String.valueOf(pngFiles.size())),
                Placeholder.unparsed("world", world.getName()),
                Placeholder.unparsed("renderer", renderer.key()),
                Placeholder.unparsed("filename", filename)
        );
    }
}
