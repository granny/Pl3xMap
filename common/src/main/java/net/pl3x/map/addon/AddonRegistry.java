package net.pl3x.map.addon;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.pl3x.map.Key;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.registry.KeyedRegistry;
import net.pl3x.map.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Manages Pl3xMap addons.
 */
public abstract class AddonRegistry extends KeyedRegistry<Addon> {
    public static final Path ADDONS_DIR = FileUtil.MAIN_DIR.resolve("addons");

    static {
        try {
            Files.createDirectories(ADDONS_DIR);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Enable all addons.
     */
    public void register() {
        Set<Path> files;
        try (Stream<Path> stream = Files.walk(ADDONS_DIR, 1)) {
            files = stream
                    .filter(path -> !Files.isDirectory(path))
                    .filter(path -> path.toString().toLowerCase(Locale.ROOT).endsWith(".jar"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (!files.isEmpty()) {
            Logger.info(Lang.ADDONS_ENABLING);
        }

        for (Path path : files) {
            try (JarFile jar = new JarFile(path.toFile())) {
                JarEntry entry = jar.getJarEntry("addon.yml");
                if (entry == null) {
                    Logger.severe("Jar file does not contain an addon.yml info file (" + path.getFileName() + ")");
                    continue;
                }

                AddonInfo info = new AddonInfo(jar.getInputStream(entry));

                if (this.entries.containsKey(info.getKey())) {
                    Logger.warn("Addon is already loaded (" + info.getName() + ")");
                    continue;
                }

                Addon addon = createAddon(path, info);
                addon.info = info;

                register(addon);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Register a new addon.
     * <p>
     * Will return null if the addon is already registered.
     *
     * @param addon addon to register
     * @return registered addon or null
     */
    @Override
    @Nullable
    public Addon register(@NotNull Addon addon) {
        if (this.entries.containsKey(addon.getKey())) {
            Logger.warn("Addon is already loaded (" + addon.getInfo().getName() + ")");
            return null;
        }
        Logger.info(Lang.ADDON_ENABLING
                .replace("<name>", addon.getInfo().getName())
                .replace("<version>", addon.getInfo().getVersion()));
        addon.enabled = true;
        try {
            addon.onEnable();
        } catch (Throwable e) {
            addon.enabled = false;
            Logger.severe(String.format("Failed to enable %s addon", addon.getName()));
            e.printStackTrace();
        }
        this.entries.put(addon.getKey(), addon);
        return addon;
    }

    /**
     * Unregister the addon for the provided key.
     * <p>
     * Will return null if no addon registered with provided key.
     *
     * @param key key
     * @return unregistered addon or null
     */
    @Override
    @Nullable
    public Addon unregister(@NotNull Key key) {
        Addon addon = super.unregister(key);
        if (addon != null) {
            Logger.info(Lang.ADDON_DISABLING
                    .replace("<name>", addon.getName())
                    .replace("<version>", addon.getVersion()));
            addon.onDisable();
            addon.enabled = false;
        }
        return addon;
    }

    /**
     * Unregister all addons.
     */
    @Override
    public void unregister() {
        Logger.info(Lang.ADDONS_DISABLING);
        super.unregister();
    }

    /**
     * Creates a new addon.
     *
     * @param path path to addon's jar
     * @param info info about the addon
     * @return a new addon
     * @throws IllegalStateException if something went wrong
     */
    @NotNull
    public abstract Addon createAddon(@NotNull Path path, @NotNull AddonInfo info);
}
