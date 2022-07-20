package net.pl3x.map.render.marker.option;

import java.util.HashMap;
import java.util.Map;

public class Stroke {
    private int weight;
    private int color;
    private double opacity;

    public Stroke(int weight, int color, double opacity) {
        this.weight = weight;
        this.color = color;
        this.opacity = opacity;
    }

    public int getWeight() {
        return this.weight;
    }

    public Stroke setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    public Stroke setColor(int color) {
        this.color = color;
        return this;
    }

    public double getOpacity() {
        return this.opacity;
    }

    public Stroke setOpacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("weight", getWeight());
        map.put("color", getColor());
        map.put("opacity", getOpacity());
        return map;
    }
}
