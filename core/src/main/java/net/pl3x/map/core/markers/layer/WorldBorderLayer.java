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

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.configuration.WorldBorderLayerConfig;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.marker.Polyline;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.markers.option.Tooltip;
import net.pl3x.map.core.util.Colors;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Manages world border marker.
 */
public class WorldBorderLayer extends WorldLayer {
    public static final String KEY = "pl3xmap_worldborder";

    private final Polyline polyline;

    /**
     * Create a new world border layer.
     *
     * @param world world
     */
    public WorldBorderLayer(@NotNull World world) {
        this(KEY, world, () -> Lang.UI_LAYER_WORLDBORDER);
        setUpdateInterval(WorldBorderLayerConfig.UPDATE_INTERVAL * 20);
        setShowControls(WorldBorderLayerConfig.SHOW_CONTROLS);
        setDefaultHidden(WorldBorderLayerConfig.DEFAULT_HIDDEN);
        setPriority(WorldBorderLayerConfig.PRIORITY);
        setZIndex(WorldBorderLayerConfig.Z_INDEX);
        setOptions(Options.builder()
                .strokeColor(Colors.fromHex(WorldBorderLayerConfig.STROKE_COLOR))
                .strokeWeight(WorldBorderLayerConfig.STROKE_WEIGHT)
                .strokeDashOffset(WorldBorderLayerConfig.STROKE_DASH_OFFSET)
                .strokeDashPattern(WorldBorderLayerConfig.STROKE_DASH_PATTERN)
                .strokeLineCapShape(WorldBorderLayerConfig.STROKE_LINE_CAP_SHAPE)
                .strokeLineJoinShape(WorldBorderLayerConfig.STROKE_LINE_JOIN_SHAPE)
                .tooltipContent(getLabel())
                .tooltipSticky(true)
                .tooltipDirection(Tooltip.Direction.TOP)
                .build());
    }

    /**
     * Create a new world border layer.
     *
     * @param key           key for layer
     * @param world         world
     * @param labelSupplier label
     */
    public WorldBorderLayer(@NotNull String key, @NotNull World world, @NotNull Supplier<@NotNull String> labelSupplier) {
        super(key, world, labelSupplier);
        this.polyline = Marker.polyline(KEY).setOptions(getOptions());
    }

    @Override
    public @NotNull Collection<@NotNull Marker<?>> getMarkers() {
        return Collections.singletonList(this.polyline.clearPoints().addPoint(
                Point.of(getWorld().getBorderMinX(), getWorld().getBorderMinZ()),
                Point.of(getWorld().getBorderMaxX(), getWorld().getBorderMinZ()),
                Point.of(getWorld().getBorderMaxX(), getWorld().getBorderMaxZ()),
                Point.of(getWorld().getBorderMinX(), getWorld().getBorderMaxZ()),
                Point.of(getWorld().getBorderMinX(), getWorld().getBorderMinZ())
        ).setOptions(getOptions()));
    }
}
