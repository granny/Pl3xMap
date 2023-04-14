package net.pl3x.map.core.registry;

import javax.management.openmbean.KeyAlreadyExistsException;
import net.pl3x.map.core.world.Biome;

public class BiomeRegistry extends Registry<Biome> {
    public Biome register(String id, int color, int foliage, int grass, int water, Biome.GrassModifier grassModifier) {
        if (has(id)) {
            throw new KeyAlreadyExistsException("Biome already registered: " + id);
        }
        return register(id, new Biome(id, color, foliage, grass, water, grassModifier));
    }

    @Override
    public Biome get(String id) {
        return getOrDefault(id, Biome.DEFAULT);
    }
}
