package net.pl3x.map.configuration;

import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.nio.file.Path;
import java.util.Arrays;
import net.pl3x.map.logger.Logger;
import org.apache.commons.lang.StringEscapeUtils;
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
            getConfig().set(path, def);
        }
        return getConfig().get(path);
    }

    protected void setComment(String path, String comment) {
        getConfig().setComment(path, comment, CommentType.BLOCK);
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
