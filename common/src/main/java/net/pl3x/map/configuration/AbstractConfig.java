package net.pl3x.map.configuration;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.util.Colors;
import org.apache.commons.lang.StringEscapeUtils;
import org.simpleyaml.configuration.ConfigurationSection;
import org.simpleyaml.configuration.MemorySection;
import org.simpleyaml.configuration.comments.CommentType;
import org.simpleyaml.configuration.file.YamlFile;
import org.simpleyaml.exceptions.InvalidConfigurationException;

public abstract class AbstractConfig {
    private YamlFile config;

    public YamlFile getConfig() {
        return this.config;
    }

    protected void reload(Path path, Class<? extends AbstractConfig> clazz) {
        // read yaml from file
        this.config = new YamlFile(path.toFile());
        try {
            getConfig().createOrLoadWithComments();
        } catch (InvalidConfigurationException e) {
            Logger.severe("Could not load " + path.getFileName() + ", please correct your syntax errors");
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // load data from yaml
        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            Key key = field.getDeclaredAnnotation(Key.class);
            Comment comment = field.getDeclaredAnnotation(Comment.class);
            if (key == null) {
                return;
            }
            try {
                Object obj = getClassObject();
                Object value = getValue(key.value(), field.get(obj));
                field.set(obj, value instanceof String str ? StringEscapeUtils.unescapeJava(str) : value);
                if (comment != null) {
                    setComment(key.value(), comment.value());
                }
            } catch (Throwable e) {
                Logger.warn("Failed to load " + key.value() + " from " + path.getFileName().toString());
                e.printStackTrace();
            }
        });

        // save yaml to disk
        try {
            getConfig().save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Object getClassObject() {
        return null;
    }

    protected Object getValue(String path, Object def) {
        if (getConfig().get(path) == null) {
            set(path, def);
        }
        return get(path);
    }

    protected void setComment(String path, String comment) {
        getConfig().setComment(path, comment, CommentType.BLOCK);
    }

    protected Object get(String path, Object def) {
        Object val = get(path);
        return val == null ? def : val;
    }

    protected Object get(String path) {
        Object value = getConfig().get(path);
        if (!(value instanceof MemorySection)) {
            return value;
        }
        // convert back to objects
        Map<Object, Object> map = new LinkedHashMap<>();
        ConfigurationSection section = getConfig().getConfigurationSection(path);
        if (section == null) {
            return map;
        }
        @SuppressWarnings("resource")
        ServerLevel level = MinecraftServer.getServer().getAllLevels().iterator().next();
        Registry<Biome> registry = level.registryAccess().registryOrThrow(Registries.BIOME);
        for (String key : section.getKeys(false)) {
            String rawValue = section.getString(key);
            if (rawValue == null) {
                continue;
            }
            ResourceLocation resource = new ResourceLocation(key);
            Biome biome = registry.get(resource);
            if (biome != null) {
                ResourceKey<Biome> resourceKey = registry.getResourceKey(biome).orElse(null);
                map.put(resourceKey, Colors.fromHex(rawValue));
            } else {
                Block block = BuiltInRegistries.BLOCK.get(resource);
                if (block != Blocks.AIR) {
                    map.put(block, Colors.fromHex(rawValue));
                } else {
                    map.put(key, rawValue);
                }
            }
        }
        return map;
    }

    protected void set(String path, Object value) {
        // only set if this path is empty
        if (value instanceof Map<?, ?> map && !map.isEmpty()) {
            // turn into strings
            map.forEach((k, v) -> {
                String key;
                Object val;
                if (k instanceof Block block) {
                    key = BuiltInRegistries.BLOCK.getKey(block).toString();
                    val = Colors.toHex((int) v);
                } else if (k instanceof ResourceKey<?> resourceKey) {
                    key = resourceKey.location().toString();
                    val = Colors.toHex((int) v);
                } else {
                    key = (String) k;
                    val = v;
                }
                getConfig().set(path + "." + key, val);
            });
        } else {
            // regular usage
            getConfig().set(path, value);
        }
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Key {
        String value();
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Comment {
        String value();
    }
}
