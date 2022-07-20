package net.pl3x.map.render.marker;

import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.render.marker.option.Options;
import net.pl3x.map.render.marker.option.Point;

public class Icon extends Marker {
    private Key image;
    private Point point;
    private Point offset;
    private int width;
    private int height;

    public Icon(Options options) {
        super(options);
    }

    public Key getImage() {
        return this.image;
    }

    public Icon setImage(Key image) {
        this.image = image;
        return this;
    }

    public Point getPoint() {
        return this.point;
    }

    public Icon setPoint(Point point) {
        this.point = point;
        return this;
    }

    public Point getOffset() {
        return this.offset;
    }

    public Icon setOffset(Point offset) {
        this.offset = offset;
        return this;
    }

    public int getWidth() {
        return this.width;
    }

    public Icon setWidth(int width) {
        this.width = width;
        return this;
    }

    public int getHeight() {
        return this.height;
    }

    public Icon setHeight(int height) {
        this.height = height;
        return this;
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "icon");
        map.put("image", getImage().getKey());
        map.put("point", getPoint().serialize());
        map.put("offset", getOffset().serialize());
        map.put("width", getWidth());
        map.put("height", getHeight());
        map.put("options", getOptions().serialize());
        return map;
    }
}
