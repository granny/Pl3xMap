package net.pl3x.map.render.marker.option;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Fill {
    private Type type;
    private int color;
    private double opacity;

    public Fill(Type type, int color, double opacity) {
        this.type = type;
        this.color = color;
        this.opacity = opacity;
    }

    public Type getType() {
        return this.type;
    }

    public Fill setType(Type type) {
        this.type = type;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    public Fill setColor(int color) {
        this.color = color;
        return this;
    }

    public double getOpacity() {
        return this.opacity;
    }

    public Fill setOpacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", getType().name().toLowerCase(Locale.ROOT));
        map.put("color", getColor());
        map.put("opacity", getOpacity());
        return map;
    }

    public enum Type {
        NONE, NONZERO, EVENODD
    }
}
