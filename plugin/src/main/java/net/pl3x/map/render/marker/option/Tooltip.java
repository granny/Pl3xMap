package net.pl3x.map.render.marker.option;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Tooltip {
    private Type type;
    private String string;
    private Point anchor;

    public Tooltip(Type type, String string, Point anchor) {
        this.type = type;
        this.string = string;
        this.anchor = anchor;
    }

    public Type getType() {
        return this.type;
    }

    public Tooltip setType(Type type) {
        this.type = type;
        return this;
    }

    public String getString() {
        return this.string;
    }

    public Tooltip setString(String string) {
        this.string = string;
        return this;
    }

    public Point getAnchor() {
        return this.anchor;
    }

    public Tooltip setAnchor(Point anchor) {
        this.anchor = anchor;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", getType().name().toLowerCase(Locale.ROOT));
        map.put("string", getString());
        map.put("anchor", getAnchor().serialize());
        return map;
    }

    public enum Type {
        CLICK, HOVER
    }
}
