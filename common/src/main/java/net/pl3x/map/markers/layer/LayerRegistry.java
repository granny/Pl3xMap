package net.pl3x.map.markers.layer;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.pl3x.map.Key;
import net.pl3x.map.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LayerRegistry extends Registry<Layer> {
    private final Map<Key, Layer> layers = new ConcurrentHashMap<>();

    @Override
    @Nullable
    public Layer register(@NotNull Key key, @NotNull Layer layer) {
        if (get(key) != null) {
            return null;
        }
        this.layers.put(key, layer);
        return layer;
    }

    @Override
    @Nullable
    public Layer unregister(@NotNull Key key) {
        return this.layers.remove(key);
    }

    @Override
    public void unregister() {
        this.layers.clear();
    }

    @Override
    @Nullable
    public Layer get(@NotNull Key key) {
        return this.layers.get(key);
    }

    @Override
    @NotNull
    public Map<Key, Layer> entries() {
        return Collections.unmodifiableMap(this.layers);
    }
}
