package net.pl3x.map.api.addon;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

public class AddonDescription {
    private final String name;
    private final String version;
    private final String description;
    private final String author;

    private final String main;

    public AddonDescription(InputStream stream) {
        Yaml yaml = new Yaml();
        Map<?, ?> map = yaml.load(stream);

        this.name = (String) map.get("name");
        this.version = (String) map.get("version");
        this.description = (String) map.get("description");
        this.author = (String) map.get("author");

        this.main = (String) map.get("main");
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public String getDescription() {
        return description;
    }

    public String getAuthor() {
        return this.author;
    }

    public String getMain() {
        return this.main;
    }
}
