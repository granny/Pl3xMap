package net.pl3x.map;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.Colors;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.serialization.ConfigurationSerializable;
import org.simpleyaml.configuration.serialization.SerializableAs;

@SerializableAs("BiomeColorMap")
public class BiomeColorMap extends LinkedHashMap<ResourceKey<Biome>, Integer> implements ConfigurationSerializable {
    @NotNull
    public static BiomeColorMap deserialize(@NotNull Map<String, Object> map) {
        Registry<Biome> registry = BiomeColors.getBiomeRegistry();
        BiomeColorMap result = new BiomeColorMap();
        for (String key : map.keySet()) {
            Biome biome = registry.get(new ResourceLocation(key));
            ResourceKey<Biome> resourceKey = biome == null ? null : registry.getResourceKey(biome).orElse(null);
            String hex = (String) map.get(key);
            Integer argb = Colors.fromHex(hex);
            result.put(resourceKey, argb);
        }
        return result;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (ResourceKey<Biome> key : keySet()) {
            String biome = key.location().toString();
            Integer argb = get(key);
            String hex = Colors.toHex(argb);
            result.put(biome, hex);
        }
        return result;
    }
}
