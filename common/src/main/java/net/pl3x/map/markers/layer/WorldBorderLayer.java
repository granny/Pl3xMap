package net.pl3x.map.markers.layer;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import net.minecraft.world.level.border.WorldBorder;
import net.pl3x.map.Key;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldBorderLayer extends SimpleLayer {
    public static final Key KEY = Key.of("pl3xmap-worldborder");
    private final World world;

    private boolean useVanilla;
    private Options options;

    public WorldBorderLayer(@NotNull Key key, @NotNull World world, @NotNull Supplier<String> labelSupplier) {
        super(key, labelSupplier);
        this.world = world;
    }

    @NotNull
    public World getWorld() {
        return this.world;
    }

    public boolean useVanilla() {
        return this.useVanilla;
    }

    @NotNull
    public WorldBorderLayer setUseVanilla(boolean useVanilla) {
        this.useVanilla = useVanilla;
        return this;
    }

    @Nullable
    public Options getOptions() {
        return this.options;
    }

    @NotNull
    public WorldBorderLayer setOptions(@Nullable Options options) {
        this.options = options;
        return this;
    }

    @Override
    @NotNull
    public Collection<Marker> getMarkers() {
        return useVanilla ? getVanillaBorderMarker() : super.getMarkers();
    }

    public Collection<Marker> getVanillaBorderMarker() {
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
