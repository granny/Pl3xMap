package net.pl3x.map.addon;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
    private final Set<String> depends = new HashSet<>();

    private final String main;

    public AddonInfo(InputStream stream) {
        Yaml yaml = new Yaml();
        Map<?, ?> map = yaml.load(stream);

        this.name = (String) map.get("name");
        this.version = (String) map.get("version");
        this.description = (String) map.get("description");
        this.author = (String) map.get("author");

        this.main = (String) map.get("main");

        if (map.containsKey("depends")) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) map.get("depends");
            this.depends.addAll(list);
        }
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
     * Get list of dependency plugins.
     * <p>
     * This does not change the load order, or guarantee the
     * dependency will be in the classpath at runtime.
     * <p>
     * It only suppresses the warnings about Pl3xMap using
     * class from other classloaders that are not in its own
     * dependency list.
     *
     * @return dependencies
     */
    public Set<String> getDepends() {
        return this.depends;
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
