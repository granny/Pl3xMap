package net.pl3x.map.api.addon;

import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.util.FileUtil;

/**
 * Manages Pl3xMap addons
 */
public class AddonManager {
    public static final Path ADDONS_DIR = FileUtil.PLUGIN_DIR.resolve("addons");

    static {
        try {
            Files.createDirectories(ADDONS_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Map<String, Addon> loadedAddons = new TreeMap<>();

    /**
     * Get a list of all loaded addons.
     *
     * @return loaded addons
     */
    public List<Addon> getAddons() {
        return loadedAddons.values().stream().toList();
    }

    /**
     * Get a specific addon by name.
     *
     * @param name name of addon
     * @return addon
     */
    public Addon getAddon(String name) {
        return this.loadedAddons.get(name);
    }

    /**
     * Enable all addons
     */
    public void enableAddons() {
        Set<Path> files;
        try (Stream<Path> stream = Files.walk(ADDONS_DIR, 1)) {
            files = stream
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".jar"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (Path path : files) {
            try (JarFile jar = new JarFile(path.toFile())) {
                JarEntry entry = jar.getJarEntry("addon.yml");
                if (entry == null) {
                    Logger.severe("Jar file does not contain an addon.yml info file (" + path.getFileName() + ")");
                    continue;
                }
                AddonInfo info = new AddonInfo(jar.getInputStream(entry));

                if (this.loadedAddons.containsKey(info.getName())) {
                    Logger.warn("Addon is already loaded (" + info.getName() + ")");
                    continue;
                }

                Logger.debug(Lang.ADDON_LOADING
                        .replace("<name>", info.getName())
                        .replace("<version>", info.getVersion()));

                URLClassLoader loader = new URLClassLoader(new URL[]{path.toFile().toURI().toURL()}, Pl3xMapPlugin.class.getClassLoader());
                Class<?> jarClass = Class.forName(info.getMain(), true, loader);
                Class<? extends Addon> clazz = jarClass.asSubclass(Addon.class);
                Addon addon = clazz.getDeclaredConstructor().newInstance();

                Field desc = Addon.class.getDeclaredField("info");
                desc.setAccessible(true);
                desc.set(addon, info);

                Logger.info(Lang.ADDON_ENABLING
                        .replace("<name>", info.getName())
                        .replace("<version>", info.getVersion()));

                addon.onEnable();

                this.loadedAddons.put(addon.getName(), addon);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Disable all addons
     */
    public void disableAddons() {
        Iterator<Map.Entry<String, Addon>> iter = this.loadedAddons.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, Addon> entry = iter.next();
            Addon addon = entry.getValue();
            Logger.info(Lang.ADDON_DISABLING
                    .replace("<name>", addon.getName())
                    .replace("<version>", addon.getVersion()));
            addon.onDisable();
            iter.remove();
        }
    }
}
