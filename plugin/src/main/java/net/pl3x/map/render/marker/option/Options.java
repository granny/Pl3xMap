package net.pl3x.map.render.marker.option;

import java.util.HashMap;
import java.util.Map;

public class Options {
    private Stroke stroke;
    private Fill fill;
    private Tooltip tooltip;

    private Options(Stroke stroke, Fill fill, Tooltip tooltip) {
        this.stroke = stroke;
        this.fill = fill;
        this.tooltip = tooltip;
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public Options setStroke(Stroke stroke) {
        this.stroke = stroke;
        return this;
    }

    public Fill getFill() {
        return this.fill;
    }

    public Options setFill(Fill fill) {
        this.fill = fill;
        return this;
    }

    public Tooltip getTooltip() {
        return this.tooltip;
    }

    public Options setTooltip(Tooltip tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    public Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<>();
        map.put("stroke", getStroke().serialize());
        map.put("fill", getFill().serialize());
        map.put("tooltip", getTooltip().serialize());
        return map;
    }
}
