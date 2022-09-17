package net.pl3x.map.markers.layer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.image.IconImage;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.markers.option.Tooltip;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Manages world spawn marker.
 */
public class SpawnLayer extends WorldLayer {
    public static final Key KEY = Key.of("spawn");

    /**
     * Create a new spawn layer.
     *
     * @param world world
     */
    public SpawnLayer(@NotNull World world) {
        this(KEY, world, () -> Lang.UI_LAYER_SPAWN);
        setUpdateInterval(15);
    }

    /**
     * Create a new spawn layer.
     *
     * @param key           key for layer
     * @param world         world
     * @param labelSupplier label
     */
    public SpawnLayer(@NotNull Key key, @NotNull World world, @NotNull Supplier<String> labelSupplier) {
        super(key, world, labelSupplier);

        Path icon = World.WEB_DIR.resolve("images/icon/" + key + ".png");
        try {
            IconImage image = new IconImage(key, ImageIO.read(icon.toFile()), "png");
            Pl3xMap.api().getIconRegistry().register(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public Collection<Marker<?>> getMarkers() {
        return Collections.singletonList(Marker.icon(
                KEY, getWorld().getSpawn(), KEY, 16
        ).setOptions(Options.builder()
                .tooltipContent(getLabel())
                .tooltipDirection(Tooltip.Direction.TOP)
                .build()
        ));
    }
}
