package net.pl3x.map.heightmap;

import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class HeightmapRegistry {
    public static final Key EVEN_ODD = new Key("even_odd_heightmap");
    public static final Key MODERN = new Key("modern_heightmap");
    public static final Key OLD_SCHOOL = new Key("old_school_heightmap");

    private final Map<Key, Heightmap> heightmaps = new HashMap<>();

    public void init() {
        register(EVEN_ODD, new EvenOddHeightmap());
        register(MODERN, new ModernHeightmap());
        register(OLD_SCHOOL, new OldSchoolHeightmap());
    }

    public void register(@NotNull Key key, @NotNull Heightmap heightmap) {
        if (this.heightmaps.containsKey(key)) {
            throw new IllegalStateException("Heightmap already registered (" + key + ")");
        }
        this.heightmaps.put(key, heightmap);
    }

    public void unregister(@NotNull Key key) {
        if (this.heightmaps.remove(key) == null) {
            throw new IllegalStateException("Heightmap not registered (" + key + ")");
        }
    }

    public void unregisterAll() {
        this.heightmaps.clear();
    }

    @Nullable
    public Heightmap get(@NotNull Key key) {
        return this.heightmaps.get(key);
    }
}
