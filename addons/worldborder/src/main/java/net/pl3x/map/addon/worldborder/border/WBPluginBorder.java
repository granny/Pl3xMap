package net.pl3x.map.addon.worldborder.border;

import com.wimbli.WorldBorder.BorderData;
import com.wimbli.WorldBorder.WorldBorder;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.Vector;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;

public class WBPluginBorder extends Border {
    public WBPluginBorder(@NotNull MapWorld mapWorld) {
        super(mapWorld, BorderType.WORLDBORDER);
    }

    @Override
    public void update() {
        BorderData border = WorldBorder.plugin.getWorldBorder(getMapWorld().getWorld().getName());

        double x = border.getX();
        double z = border.getZ();
        Vector radius = Vector.of(border.getRadiusX(), border.getRadiusZ());

        if (border.getShape()) {
            this.marker = Marker.ellipse(Point.of(x, z), radius);
            return;
        }

        this.marker = Marker.polyline(
                Point.of(x - radius.getX(), z - radius.getZ()),
                Point.of(x + radius.getX(), z - radius.getZ()),
                Point.of(x + radius.getX(), z + radius.getZ()),
                Point.of(x - radius.getX(), z + radius.getZ()),
                Point.of(x - radius.getX(), z - radius.getZ())
        );
    }
}
