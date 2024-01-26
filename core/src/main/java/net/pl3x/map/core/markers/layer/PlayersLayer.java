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
package net.pl3x.map.core.markers.layer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.configuration.PlayersLayerConfig;
import net.pl3x.map.core.image.IconImage;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.marker.Icon;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.markers.option.Tooltip;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Manages player markers.
 */
public class PlayersLayer extends WorldLayer {
    public static final String KEY = "pl3xmap_players";

    private final String icon;

    /**
     * Create a new players layer.
     *
     * @param world world
     */
    public PlayersLayer(@NotNull World world) {
        this(KEY, world, () -> Lang.UI_LAYER_PLAYERS);
        setUpdateInterval(PlayersLayerConfig.UPDATE_INTERVAL, PlayersLayerConfig.UPDATE_INTERVAL_IN_TICKS);
        setShowControls(PlayersLayerConfig.SHOW_CONTROLS);
        setDefaultHidden(PlayersLayerConfig.DEFAULT_HIDDEN);
        setPriority(PlayersLayerConfig.PRIORITY);
        setZIndex(PlayersLayerConfig.Z_INDEX);
        setPane(PlayersLayerConfig.PANE);
        setCss(PlayersLayerConfig.CSS);
    }

    /**
     * Create a new players layer.
     *
     * @param key           key for layer
     * @param world         world
     * @param labelSupplier label
     */
    public PlayersLayer(@NotNull String key, @NotNull World world, @NotNull Supplier<@NotNull String> labelSupplier) {
        super(key, world, labelSupplier);

        this.icon = PlayersLayerConfig.ICON;

        Path player = FileUtil.getWebDir().resolve("images/icon/" + this.icon + ".png");
        try {
            IconImage playerImage = new IconImage(this.icon, ImageIO.read(player.toFile()), "png");
            Pl3xMap.api().getIconRegistry().register(playerImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Collection<@NotNull Marker<?>> getMarkers() {
        Set<Marker<?>> icons = new HashSet<>();
        getWorld().getPlayers().forEach(player -> {
            if (player.isHidden()) {
                return;
            }
            if (player.isNPC()) {
                return;
            }
            if (PlayersLayerConfig.HIDE_INVISIBLE && player.isInvisible()) {
                return;
            }
            if (PlayersLayerConfig.HIDE_SPECTATORS && player.isSpectator()) {
                return;
            }
            icons.add(createIcon(player));
        });
        return icons;
    }

    private @NotNull Icon createIcon(@NotNull Player player) {
        Icon icon = Marker.icon(player.getUUID().toString(), player.getPosition(), this.icon, 16)
                .setRotationAngle((double) player.getYaw())
                .setRotationOrigin("center")
                .setPane("players");
        String tooltip = PlayersLayerConfig.TOOLTIP;
        if (tooltip == null || tooltip.isBlank()) {
            return icon;
        }
        return icon.setOptions(Options.builder()
                .tooltipContent(tooltip
                        .replace("<uuid>", player.getUUID().toString())
                        .replace("<name>", player.getName())
                        .replace("<decoratedName>", player.getDecoratedName())
                        .replace("<health>", Integer.toString(player.getHealth()))
                        .replace("<armor>", Integer.toString(player.getArmorPoints()))
                )
                .tooltipPane(PlayersLayerConfig.PANE)
                .tooltipDirection(Tooltip.Direction.RIGHT)
                .tooltipPermanent(true)
                .tooltipOffset(Point.of(5, 0))
                .tooltipOpacity(1.0D)
                .build()
        );
    }
}
