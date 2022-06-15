package net.pl3x.map.configuration;

import net.pl3x.map.logger.Logger;
import net.pl3x.map.util.FileUtil;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
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

    public void reload(Path path, Class<? extends AbstractConfig> clazz) {
        this.config = new YamlConfiguration();

        File file = path.toFile();
        String filename = path.getFileName().toString();

        try {
            this.config.load(file);
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException e) {
            Logger.severe("Could not load " + filename + ", please correct your syntax errors");
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        config.options().copyDefaults(true);
        config.options().width(9999);

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            Key key = field.getDeclaredAnnotation(Key.class);
            if (key == null) {
                return;
            }
            try {
                Object classObj = getClassObject();
                Object value = getValue(key.value(), field.get(classObj));
                field.set(classObj, value instanceof String str ? StringEscapeUtils.unescapeJava(str) : value);
            } catch (IllegalAccessException e) {
                Logger.warn("Failed to load " + filename);
                e.printStackTrace();
            }
            Comment comment = field.getDeclaredAnnotation(Comment.class);
            if (comment == null) {
                return;
            }
            this.config.setComments(key.value(), Arrays.stream(comment.value().split("\n")).toList());
        });

        try {
            config.save(file);
        } catch (IOException e) {
            Logger.severe("Could not save " + path);
            e.printStackTrace();
        }
    }

    protected Object getClassObject() {
        return null;
    }

    private Object getValue(String path, Object def) {
        this.config.addDefault(path, def);
        return this.config.get(path, this.config.get(path));
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
