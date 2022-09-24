package net.pl3x.map.addon.griefprevention.layer;

import java.util.Collection;
import net.pl3x.map.Key;
import net.pl3x.map.addon.griefprevention.GriefPrevention;
import net.pl3x.map.addon.griefprevention.configuration.Config;
import net.pl3x.map.markers.layer.SimpleLayer;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public class GPLayer extends SimpleLayer {
    public static final Key KEY = Key.of("griefprevention");

    private final GriefPrevention addon;
    private final World world;

    /**
     * Create a new GriefPrevention layer.
     */
    public GPLayer(GriefPrevention addon, @NotNull World world) {
        super(KEY, () -> Config.LAYER_LABEL);
        this.addon = addon;
        this.world = world;
        setShowControls(Config.LAYER_SHOW_CONTROLS);
        setDefaultHidden(Config.LAYER_DEFAULT_HIDDEN);
        setUpdateInterval(Config.LAYER_UPDATE_INTERVAL);
        setPriority(Config.LAYER_PRIORITY);
        setZIndex(Config.LAYER_ZINDEX);
    }

    @Override
    @NotNull
    public Collection<Marker<?>> getMarkers() {
        return this.addon.getGPHook().getClaims(this.world);
    }
}
