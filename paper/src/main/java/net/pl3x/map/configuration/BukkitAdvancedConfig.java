package net.pl3x.map.configuration;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.Colors;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.MemorySection;

public class BukkitAdvancedConfig extends BukkitAbstractConfig {
    private static final BukkitAdvancedConfig CONFIG = new BukkitAdvancedConfig();

    public static void reload() {
        // this has to extract before advanced config to load biome colors correctly
        FileUtil.extract("/web/", MapWorld.WEB_DIR, !Config.WEB_DIR_READONLY);

        CONFIG.reload(FileUtil.MAIN_DIR.resolve("advanced.yml"), AdvancedConfig.class);
    }

    protected Object getValue(String path, Object def) {
        if (getConfig().get(path) == null) {
            // only set if this path is empty
            if (def instanceof Map<?, ?> map && !map.isEmpty()) {
                // turn into strings
                map.forEach((k, v) -> {
                    String key, hex = Colors.toHex((int) v);
                    if (k instanceof Block block) {
                        key = Registry.BLOCK.getKey(block).toString();
                    } else {
                        key = ((ResourceKey<?>) k).location().toString();
                    }
                    getConfig().set(path + "." + key, hex);
                });
            } else {
                // regular usage
                getConfig().set(path, def);
            }
        }

        Object value = getConfig().get(path);

        if (value instanceof MemorySection) {
            // convert back to objects
            Map<Object, Object> sanitized = new LinkedHashMap<>();
            ConfigurationSection section = getConfig().getConfigurationSection(path);
            if (section != null) {
                Registry<Biome> registry = BiomeColors.getBiomeRegistry(MinecraftServer.getServer().getAllLevels().iterator().next());
                for (String key : section.getKeys(false)) {
                    String hex = section.getString(key);
                    if (hex == null) {
                        continue;
                    }
                    ResourceLocation resource = new ResourceLocation(key);
                    Biome biome = registry.get(resource);
                    ResourceKey<Biome> resourceKey = biome == null ? null : registry.getResourceKey(biome).orElse(null);
                    sanitized.put(resourceKey != null ? resourceKey : Registry.BLOCK.get(resource), Colors.fromHex(hex));
                }
            }
            value = sanitized;
        }

        return value;
    }
}
