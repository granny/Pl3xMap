package net.pl3x.map.render;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;
import javax.imageio.ImageIO;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.image.IconImage;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a holder for a renderer.
 */
public class RendererHolder extends Keyed {
    private final String name;
    private final Class<? extends Renderer> clazz;

    /**
     * Create a new renderer holder.
     *
     * @param key   identifying key
     * @param name  name of renderer
     * @param clazz the renderer class
     */
    public RendererHolder(@NotNull Key key, @NotNull String name, @NotNull Class<? extends Renderer> clazz) {
        this(key, name, clazz, true);
    }

    /**
     * Create a new renderer holder.
     *
     * @param key          identifying key
     * @param name         name of renderer
     * @param clazz        the renderer class
     * @param registerIcon true tro register icon
     */
    public RendererHolder(@NotNull Key key, @NotNull String name, @NotNull Class<? extends Renderer> clazz, boolean registerIcon) {
        super(key);
        this.name = name;
        this.clazz = clazz;

        if (!registerIcon) {
            return;
        }

        Path icon = World.WEB_DIR.resolve("images/icon/" + key + ".png");
        try {
            IconImage image = new IconImage(key, ImageIO.read(icon.toFile()), "png");
            Pl3xMap.api().getIconRegistry().register(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get the name of this renderer.
     *
     * @return renderer name
     */
    @NotNull
    public String getName() {
        return this.name;
    }

    /**
     * Get the class for the renderer.
     *
     * @return renderer class
     */
    @NotNull
    public Class<? extends Renderer> getClazz() {
        return this.clazz;
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
        RendererHolder other = (RendererHolder) o;
        return getKey() == other.getKey()
                && getName().equals(other.getName())
                && getClazz() == other.getClazz();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKey(), getName(), getClazz());
    }

    @Override
    public String toString() {
        return "RendererHolder{"
                + "key=" + getKey()
                + ",name=" + getName()
                + ",clazz=" + getClazz()
                + "}";
    }
}
