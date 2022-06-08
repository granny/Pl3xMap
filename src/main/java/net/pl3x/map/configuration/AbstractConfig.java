package net.pl3x.map.configuration;

import net.pl3x.map.util.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.Collections;

public class AbstractConfig {
    private YamlConfiguration config;

    public void reload(File configFile, Class<? extends AbstractConfig> clazz) {
        this.config = new YamlConfiguration();

        try {
            this.config.load(configFile.getCanonicalFile());
        } catch (IOException ignore) {
        } catch (InvalidConfigurationException e) {
            Logger.severe("Could not load " + configFile.getName() + ", please correct your syntax errors", e);
            throw new RuntimeException(e);
        }

        this.config.options().copyDefaults(true);
        this.config.options().width(9999); // don't split long lines, smh my head
        this.config.options().setHeader(Collections.emptyList()); // TODO - header pointing to wiki

        Arrays.stream(clazz.getDeclaredFields()).forEach(field -> {
            Key key = field.getDeclaredAnnotation(Key.class);
            if (key != null) {
                try {
                    Object value = get(key.value(), field.get(null));
                    field.set(null, value instanceof String str ? StringEscapeUtils.unescapeJava(str) : value);
                } catch (IllegalAccessException e) {
                    Logger.warn("Failed to load " + configFile.getName(), e);
                }
            }
        });

        try {
            this.config.save(configFile);
        } catch (IOException e) {
            Logger.severe("Could not save " + configFile.getName(), e);
        }
    }

    private Object get(String path, Object def) {
        this.config.addDefault(path, def);
        return this.config.get(path, this.config.get(path));
    }

    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    protected @interface Key {
        String value();
    }
}
