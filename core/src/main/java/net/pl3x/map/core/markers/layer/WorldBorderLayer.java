package net.pl3x.map.core.markers.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.markers.option.Tooltip;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Manages world border marker.
 */
public class WorldBorderLayer extends WorldLayer {
    public static final String KEY = "worldborder";

    /**
     * Create a new world border layer.
     *
     * @param world world
     */
    public WorldBorderLayer(@NonNull World world) {
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
    public WorldBorderLayer(@NonNull String key, @NonNull World world, @NonNull Supplier<String> labelSupplier) {
        super(key, world, labelSupplier);
    }

    @Override
    @NonNull
    public Collection<Marker<?>> getMarkers() {
        World.Border border = getWorld().getWorldBorder();

        int x = (int) border.centerX();
        int z = (int) border.centerZ();
        int r = (int) border.size() / 2;

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
