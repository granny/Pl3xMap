package net.pl3x.map.render.marker;

import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.render.marker.option.Options;

public class Ellipse extends Marker {
    public Ellipse(Options options) {
        super(options);
    }

    @Override
    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("type", "ellipse");
        map.put("options", getOptions().serialize());
        return map;
    }
}
