package net.pl3x.map.api.addon;

import java.io.InputStream;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * This is the runtime-container for the information in the
 * addon.yml file of an addon. All addons must have a respective
 * addon.yml in the root of the jar file.
 */
public class AddonInfo {
    private final String name;
    private final String version;
    private final String description;
    private final String author;

    private final String main;

    public AddonInfo(InputStream stream) {
        Yaml yaml = new Yaml();
        Map<?, ?> map = yaml.load(stream);

        this.name = (String) map.get("name");
        this.version = (String) map.get("version");
        this.description = (String) map.get("description");
        this.author = (String) map.get("author");

        this.main = (String) map.get("main");
    }

    /**
     * Get this addon's name.
     *
     * @return name of addon
     */
    public String getName() {
        return this.name;
    }

    /**
     * Get this addon's version.
     *
     * @return version of addon
     */
    public String getVersion() {
        return this.version;
    }

    /**
     * Get this addon's description.
     *
     * @return description of addon
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get this addon's author.
     *
     * @return author of addon
     */
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get this addon's main class.
     *
     * @return main class of addon
     */
    public String getMain() {
        return this.main;
    }
}
