package net.pl3x.map.addon;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import net.pl3x.map.PaperPl3xMap;
import org.bukkit.plugin.java.JavaPluginLoader;
import org.bukkit.plugin.java.PluginClassLoader;
import org.jetbrains.annotations.NotNull;

public class BukkitAddonRegistry extends AddonRegistry {
    @Override
    @NotNull
    public Addon createAddon(@NotNull Path path, @NotNull AddonInfo info) {
        try {
            URLClassLoader loader = new URLClassLoader(new URL[]{path.toFile().toURI().toURL()}, PaperPl3xMap.class.getClassLoader());
            Class<?> jarClass = Class.forName(info.getMain(), true, loader);
            Class<? extends Addon> clazz = jarClass.asSubclass(Addon.class);

            Addon addon = clazz.getDeclaredConstructor().newInstance();

            /*Set<String> depends = info.getDepends();
            if (!depends.isEmpty()) {
                Field loaders = JavaPluginLoader.class.getDeclaredField("loaders");
                loaders.setAccessible(true);
                @SuppressWarnings("unchecked")
                List<PluginClassLoader> list = (List<PluginClassLoader>) loaders.get(PaperPl3xMap.getInstance().getPluginLoader());

                Field field = PluginClassLoader.class.getDeclaredField("seenIllegalAccess");
                field.setAccessible(true);

                for (PluginClassLoader classLoader : list) {
                    @SuppressWarnings("unchecked")
                    Set<String> set = (Set<String>) field.get(classLoader);
                    set.addAll(depends);
                }
            }*/

            return addon;
        } catch (Throwable e) {
            throw new IllegalStateException(e);
        }
    }
}
