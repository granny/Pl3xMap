package net.pl3x.map.addon.worldborder.border;

import java.util.List;
import java.util.stream.Collectors;
import net.pl3x.map.api.markers.Point;
import net.pl3x.map.api.markers.marker.Marker;
import net.pl3x.map.world.MapWorld;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @Nullable
    public Marker getMarker() {
        BorderData border = ChunkyBorderProvider.get().getBorder(getMapWorld().getName()).orElse(null);
        if (border == null) {
            return null;
        }

        Shape shape = border.getBorder();
        if (shape instanceof AbstractPolygon polygon) {
            List<Vector2> allPoints = polygon.points();
            List<Point> points = allPoints.stream()
                    .map(point -> Point.of(point.getX(), point.getZ()))
                    .collect(Collectors.toList());
            Vector2 lastPoint = allPoints.get(0);
            points.add(Point.of(lastPoint.getX(), lastPoint.getZ()));
            return Marker.polyline(points);
        }

        if (shape instanceof AbstractEllipse ellipse) {
            Vector2 center = ellipse.center();
            Vector2 radii = ellipse.radii();
            if (ellipse instanceof Circle) {
                return Marker.circle(center.getX(), center.getZ(), radii.getX());
            } else {
                return Marker.ellipse(center.getX(), center.getZ(), radii.getX(), radii.getZ());
            }
        }

        return null;
    }
}
