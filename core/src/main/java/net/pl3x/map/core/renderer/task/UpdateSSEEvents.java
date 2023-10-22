package net.pl3x.map.core.renderer.task;

import java.util.ArrayList;
import java.util.List;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.httpd.HttpdServer;
import net.pl3x.map.core.markers.layer.Layer;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.world.World;

public class UpdateSSEEvents extends AbstractDataTask {
    public UpdateSSEEvents(World world) {
        super(0, true, world, "Pl3xMap-SSE-Events");
    }

    @Override
    public void parse() {
        if (!Config.SSE_EVENTS) {
            return;
        }

        List<Object> layers = new ArrayList<>();

        this.world.getLayerRegistry().entrySet().forEach(entry -> {
            String key = entry.getKey();
            Layer layer = entry.getValue();
            List<Marker<?>> list = new ArrayList<>(layer.getMarkers());
            HttpdServer.sendSSE("markers", String.format("{ \"world\": \"%s\", \"key\": \"%s\", \"markers\": %s}", this.world.getName(), key, this.gson.toJson(list)));
        });

        HttpdServer.sendSSE("players", this.gson.toJson(Pl3xMap.api().getPlayerRegistry().parsePlayers()));
    }
}
