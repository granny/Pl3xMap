package net.pl3x.map.addon.worldborder.border;

import net.minecraft.world.level.border.WorldBorder;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public class VanillaBorder extends Border {
    public VanillaBorder(@NotNull World world) {
        super(world, BorderType.VANILLA);
    }

    @Override
    public void update() {
        WorldBorder border = getWorld().getLevel().getWorldBorder();

        int x = (int) border.getCenterX();
        int z = (int) border.getCenterZ();
        int r = (int) border.getSize() / 2;

        this.marker = Marker.polyline(
                Point.of(x - r, z - r),
                Point.of(x + r, z - r),
                Point.of(x + r, z + r),
                Point.of(x - r, z + r),
                Point.of(x - r, z - r)
        );
    }
}
