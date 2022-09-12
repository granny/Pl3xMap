package net.pl3x.map.markers.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.world.level.border.WorldBorder;
import net.pl3x.map.Key;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Manages world border marker.
 */
public class WorldBorderLayer extends WorldLayer {
    public static final Key KEY = Key.of("pl3xmap-worldborder");

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
                Point.of(x - r, z - r),
                Point.of(x + r, z - r),
                Point.of(x + r, z + r),
                Point.of(x - r, z + r),
                Point.of(x - r, z - r)
        ).setOptions(getOptions()));
    }
}
