package net.pl3x.map.configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import net.pl3x.map.logger.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public abstract class BukkitAbstractConfig extends AbstractConfig {
    protected YamlConfiguration config;

    public YamlConfiguration getConfig() {
        return this.config;
    }

    public void reload(Path path, Class<? extends AbstractConfig> clazz) {
        this.config = new YamlConfiguration();

        this.config.options().copyDefaults(true);
        this.config.options().parseComments(true);
        this.config.options().width(9999);

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

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            Key key = field.getDeclaredAnnotation(Key.class);
            Comment comment = field.getDeclaredAnnotation(Comment.class);
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
            if (comment != null) {
                setComments(key.value(), Arrays.stream(comment.value().split("\n")).toList());
            }
        });

        try {
            this.config.save(file);
        } catch (IOException e) {
            Logger.severe("Could not save " + path);
            e.printStackTrace();
        }
    }

    protected Object getClassObject() {
        return null;
    }

    protected Object getValue(String path, Object def) {
        if (this.config.get(path) == null) {
            this.config.set(path, def);
        }
        return this.config.get(path);
    }

    protected void setComments(String path, List<String> comments) {
        this.config.setComments(path, comments);
    }
}
