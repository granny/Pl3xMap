package net.pl3x.map.palette;

import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.Util;
import net.minecraft.locale.Language;
import net.minecraft.resources.ResourceLocation;
import net.pl3x.map.registry.Registry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a palette registry
 *
 * @param <T>
 */
public class PaletteRegistry<T> extends Registry<T, Palette> {
    private boolean locked;

    /**
     * Lock the palette.
     */
    public void lock() {
        this.locked = true;
    }

    /**
     * Check if palette is locked.
     *
     * @return true if locked
     */
    public boolean isLocked() {
        return this.locked;
    }

    /**
     * Register a new index.
     * <p>
     * Will return null if the index is already registered.
     *
     * @param key     key to register
     * @param palette index to register
     * @return registered index or null
     * @throws IllegalStateException if palette is locked
     */
    @Nullable
    public Palette register(@NotNull T key, @NotNull Palette palette) {
        if (isLocked()) {
            throw new IllegalStateException("Palette is locked");
        }
        return super.register(key, palette);
    }

    /**
     * Get the index map of this palette.
     *
     * @return index map
     */
    public Map<Integer, String> getMap() {
        Map<Integer, String> result = new LinkedHashMap<>();
        for (Palette entry : this.entries.values()) {
            Integer index = entry.getIndex();
            String name = entry.getName();
            result.put(index, name);
        }
        return result;
    }

    public static String toName(String type, ResourceLocation key) {
        return Language.getInstance().getOrDefault(Util.makeDescriptionId(type, key));
    }
}
