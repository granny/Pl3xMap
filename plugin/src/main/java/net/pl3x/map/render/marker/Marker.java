package net.pl3x.map.render.marker;

import java.util.Map;
import net.pl3x.map.render.marker.option.Options;

public abstract class Marker {
    private final Options options;

    public Marker(Options options) {
        this.options = options;
    }

    public Options getOptions() {
        return this.options;
    }

    public abstract Map<String, Object> serialize();
}
