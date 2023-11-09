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
import java.util.Collections;
import javax.imageio.ImageIO;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.configuration.SpawnLayerConfig;
import net.pl3x.map.core.image.IconImage;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.markers.option.Tooltip;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Manages world spawn marker.
 */
public class SpawnLayer extends WorldLayer {
    public static final String KEY = "pl3xmap_spawn";

    private final String icon;

    /**
     * Create a new spawn layer.
     *
     * @param world world
     */
    public SpawnLayer(@NotNull World world) {
        super(KEY, world, () -> Lang.UI_LAYER_SPAWN);

        this.icon = SpawnLayerConfig.ICON;

        Path icon = FileUtil.getWebDir().resolve("images/icon/" + this.icon + ".png");
        try {
            IconImage image = new IconImage(this.icon, ImageIO.read(icon.toFile()), "png");
            Pl3xMap.api().getIconRegistry().register(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setUpdateInterval(SpawnLayerConfig.UPDATE_INTERVAL);
        setShowControls(SpawnLayerConfig.SHOW_CONTROLS);
        setDefaultHidden(SpawnLayerConfig.DEFAULT_HIDDEN);
        setPriority(SpawnLayerConfig.PRIORITY);
        setZIndex(SpawnLayerConfig.Z_INDEX);

        String tooltip = getLabel();
        if (!tooltip.isBlank()) {
            setOptions(Options.builder()
                    .tooltipContent(tooltip)
                    .tooltipDirection(Tooltip.Direction.TOP)
                    .build()
            );
        }
    }

    @Override
    public @NotNull Collection<@NotNull Marker<?>> getMarkers() {
        return Collections.singletonList(Marker.icon(KEY, getWorld().getSpawn(), this.icon, 16).setOptions(getOptions()));
    }
}
