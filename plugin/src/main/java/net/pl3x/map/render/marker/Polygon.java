package net.pl3x.map.render.marker;

import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.render.marker.option.Options;

public class Polygon extends Marker {
    public Polygon(Options options) {
        super(options);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "polygon");
        map.put("options", getOptions().serialize());
        return map;
    }
}
