package net.pl3x.map.markers.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.world.level.border.WorldBorder;
import net.pl3x.map.Key;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.markers.option.Tooltip;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Manages world border marker.
 */
public class WorldBorderLayer extends WorldLayer {
    public static final Key KEY = Key.of("worldborder");

    /**
     * Create a new world border layer.
     *
     * @param world world
     */
    public WorldBorderLayer(@NotNull World world) {
        this(KEY, world, () -> Lang.UI_LAYER_WORLDBORDER);
        setUpdateInterval(15);
        setShowControls(true);
        setDefaultHidden(false);
        setPriority(99);
        setZIndex(99);
        setOptions(Options.builder()
                .strokeColor(0xFFFF0000)
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
    public WorldBorderLayer(@NotNull Key key, @NotNull World world, @NotNull Supplier<String> labelSupplier) {
        super(key, world, labelSupplier);
    }

    @Override
    @NotNull
    public Collection<Marker<?>> getMarkers() {
        WorldBorder border = getWorld().getLevel().getWorldBorder();

        int x = (int) border.getCenterX();
        int z = (int) border.getCenterZ();
        int r = (int) border.getSize() / 2;

        return Collections.singletonList(Marker.polyline(
                KEY,
                Point.of(x - r, z - r),
                Point.of(x + r, z - r),
                Point.of(x + r, z + r),
                Point.of(x - r, z + r),
                Point.of(x - r, z - r)
        ).setOptions(Options.builder()
                .tooltipContent(getLabel())
                .tooltipDirection(Tooltip.Direction.TOP)
                .build()
        ));
    }
}
