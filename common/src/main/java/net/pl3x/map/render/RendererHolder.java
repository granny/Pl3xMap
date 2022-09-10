package net.pl3x.map.render;

import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a holder for a renderer.
 */
public class RendererHolder extends Keyed {
    private final Class<? extends Renderer> clazz;

    /**
     * Create a new renderer holder.
     *
     * @param key key for renderer holder
     */
    public RendererHolder(@NotNull Key key, @NotNull Class<? extends Renderer> clazz) {
        super(key);
        this.clazz = clazz;
    }

    @NotNull
    public Class<? extends Renderer> getClazz() {
        return this.clazz;
    }
}
