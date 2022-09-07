package net.pl3x.map;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.util.Colors;
import org.jetbrains.annotations.NotNull;
import org.simpleyaml.configuration.serialization.ConfigurationSerializable;
import org.simpleyaml.configuration.serialization.SerializableAs;

@SerializableAs("BlockColorMap")
public class BlockColorMap extends LinkedHashMap<Block, Integer> implements ConfigurationSerializable {
    @NotNull
    public static BlockColorMap deserialize(@NotNull Map<String, Object> map) {
        BlockColorMap result = new BlockColorMap();
        for (String key : map.keySet()) {
            Block block = Registry.BLOCK.get(new ResourceLocation(key));
            String hex = (String) map.get(key);
            Integer argb = Colors.fromHex(hex);
            result.put(block, argb);
        }
        return result;
    }

    @Override
    @NotNull
    public Map<String, Object> serialize() {
        Map<String, Object> result = new LinkedHashMap<>();
        for (Block key : keySet()) {
            String block = Registry.BLOCK.getKey(key).toString();
            Integer argb = get(key);
            String hex = Colors.toHex(argb);
            result.put(block, hex);
        }
        return result;
    }
}
