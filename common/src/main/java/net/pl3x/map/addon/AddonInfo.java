package net.pl3x.map.addon;

import java.io.InputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

/**
 * This is the runtime-container for the information in the
 * addon.yml file of an addon. All addons must have a respective
 * addon.yml in the root of the jar file.
 */
public class AddonInfo extends Keyed {
    private final Key key;
    private final String name;
    private final String version;
    private final String description;
    private final String author;
    private final String website;
    private final Set<String> depends = new HashSet<>();

    private final String main;

    AddonInfo(InputStream stream) {
        super(Key.NONE);

        Yaml yaml = new Yaml();
        Map<?, ?> map = yaml.load(stream);

        this.name = (String) map.get("name");
        this.key = new Key(getName());
        this.version = (String) map.get("version");

        this.description = (String) map.get("description");
        this.author = (String) map.get("author");
        this.website = (String) map.get("website");

        this.main = (String) map.get("main");

        if (map.containsKey("depends")) {
            @SuppressWarnings("unchecked")
            List<String> list = (List<String>) map.get("depends");
            this.depends.addAll(list);
        }
    }

    @Override
    @NotNull
    public Key getKey() {
        return this.key;
    }

    /**
     * Get this addon's name.
     *
     * @return name of addon
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Get this addon's version.
     *
     * @return version of addon
     */
    @NotNull
    public String getVersion() {
        return this.version;
    }

    /**
     * Get this addon's description.
     *
     * @return description of addon
     */
    @Nullable
    public String getDescription() {
        return description;
    }

    /**
     * Get this addon's author.
     *
     * @return author of addon
     */
    @Nullable
    public String getAuthor() {
        return this.author;
    }

    /**
     * Get this addon's website.
     *
     * @return website of addon
     */
    @Nullable
    public String getWebsite() {
        return this.website;
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
    @NotNull
    public Set<String> getDepends() {
        return this.depends;
    }

    /**
     * Get this addon's main class.
     *
     * @return main class of addon
     */
    @NotNull
    public String getMain() {
        return this.main;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        AddonInfo other = (AddonInfo) o;
        return getKey().equals(other.getKey())
                && getName().equals(other.getName())
                && getVersion().equals(other.getVersion())
                && Objects.equals(getDescription(), other.getDescription())
                && Objects.equals(getAuthor(), other.getAuthor())
                && Objects.equals(getWebsite(), other.getWebsite())
                && getDepends().equals(other.getDepends())
                && getMain().equals(other.getMain());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getName(), getVersion(), getDescription(), getAuthor(), getWebsite(), getDepends(), getMain());
    }

    @Override
    public String toString() {
        return "RendererType{"
                + "key=" + getKey()
                + ",name=" + getName()
                + ",version=" + getVersion()
                + ",description=" + getDescription()
                + ",author=" + getAuthor()
                + ",website=" + getWebsite()
                + ",depends=" + getDepends()
                + ",main=" + getMain()
                + "}";
    }
}
