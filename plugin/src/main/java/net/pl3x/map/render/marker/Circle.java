package net.pl3x.map.render.marker;

import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.render.marker.option.Options;

public class Circle extends Marker {
    public Circle(Options options) {
        super(options);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "circle");
        map.put("options", getOptions().serialize());
        return map;
    }
}
