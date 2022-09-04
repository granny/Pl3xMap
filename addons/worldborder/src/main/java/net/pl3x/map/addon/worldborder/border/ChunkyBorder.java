package net.pl3x.map.addon.worldborder.border;

import java.util.List;
import java.util.stream.Collectors;
import net.pl3x.map.api.markers.Line;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;
import org.popcraft.chunky.platform.util.Vector2;
import org.popcraft.chunky.shape.AbstractEllipse;
import org.popcraft.chunky.shape.AbstractPolygon;
import org.popcraft.chunky.shape.Circle;
import org.popcraft.chunky.shape.Shape;
import org.popcraft.chunkyborder.BorderData;
import org.popcraft.chunkyborder.ChunkyBorderProvider;

public class ChunkyBorder extends Border {
    public ChunkyBorder(@NotNull MapWorld mapWorld) {
        super(mapWorld, BorderType.CHUNKY);
    }

    @Override
    public void update() {
        BorderData border = ChunkyBorderProvider.get().getBorder(getMapWorld().getName()).orElse(null);
        if (border == null) {
            this.marker = null;
            return;
        }

        Shape shape = border.getBorder();

        if (shape instanceof AbstractPolygon polygon) {
            List<Point> points = polygon.points().stream()
                    .map(point -> Point.of(point.getX(), point.getZ()))
                    .collect(Collectors.toList());
            this.marker = Marker.polyline(new Line(points).loop());
            return;
        }

        if (shape instanceof AbstractEllipse ellipse) {
            Vector2 center = ellipse.center();
            Vector2 radii = ellipse.radii();
            if (ellipse instanceof Circle) {
                this.marker = Marker.circle(center.getX(), center.getZ(), radii.getX());
            } else {
                this.marker = Marker.ellipse(center.getX(), center.getZ(), radii.getX(), radii.getZ());
            }
            return;
        }

        this.marker = null;
    }
}
