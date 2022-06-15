package net.pl3x.map.configuration;

import net.pl3x.map.logger.Logger;
import net.pl3x.map.util.FileUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.Arrays;

public class AbstractConfig {
    public static final Path DATA_DIR = FileUtil.PLUGIN_DIR.resolve("data");
    public static final Path LOCALE_DIR = FileUtil.PLUGIN_DIR.resolve("locale");
    public static final Path RENDERER_DIR = FileUtil.PLUGIN_DIR.resolve("renderer");
    public static final Path WORLD_DIR = FileUtil.PLUGIN_DIR.resolve("world");

    private YamlConfiguration config;

    public void reload(Path configFile, Class<? extends AbstractConfig> clazz) {
        this.config = new YamlConfiguration();

        try {
            this.config.load(configFile.toFile());
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException e) {
            Logger.severe("Could not load " + configFile.getFileName() + ", please correct your syntax errors");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            Key key = field.getDeclaredAnnotation(Key.class);
            if (key == null) {
                return;
            }
            try {
                Object value = this.config.get(key.value());
                field.set(getClassObject(), value instanceof String str ? StringEscapeUtils.unescapeJava(str) : value);
            } catch (IllegalAccessException e) {
                Logger.warn("Failed to load " + configFile.getFileName());
                e.printStackTrace();
            }
        });
    }

    protected Object getClassObject() {
        return null;
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Key {
        String value();
    }
}
