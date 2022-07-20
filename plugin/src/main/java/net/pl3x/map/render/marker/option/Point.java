package net.pl3x.map.render.marker.option;

import java.util.HashMap;
import java.util.Map;

public class Point {
    private int x;
    private int z;

    public Point(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public int getX() {
        return this.x;
    }

    public Point setX(int x) {
        this.x = x;
        return this;
    }

    public int getZ() {
        return this.z;
    }

    public Point setZ(int z) {
        this.z = z;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("x", getX());
        map.put("z", getZ());
        return map;
    }
}
