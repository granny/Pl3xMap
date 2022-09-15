package net.pl3x.map.markers.layer;

import java.io.IOException;
import java.io.InputStream;
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
    public static final Key KEY = Key.of("spawn-icon");

    /**
     * Create a new spawn layer.
     *
     * @param key           key for layer
     * @param world         world
     * @param labelSupplier label
     */
    public SpawnLayer(@NotNull Key key, @NotNull World world, @NotNull Supplier<String> labelSupplier) {
        super(key, world, labelSupplier);

        try (InputStream in = getClass().getResourceAsStream("/web/images/icon/spawn.png")) {
            if (in == null) {
                throw new RuntimeException("Could not read spawn image from jar!");
            }
            Pl3xMap.api().getIconRegistry().register(new IconImage(KEY, ImageIO.read(in), "png"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setOptions(Options.builder()
                .tooltipContent(Lang.UI_WORLD_SPAWN)
                .tooltipDirection(Tooltip.Direction.TOP)
                .build());
    }

    @Override
    @NotNull
    public Collection<Marker<?>> getMarkers() {
        return Collections.singletonList(Marker.icon(getWorld().getSpawn(), KEY, 16).setOptions(getOptions()));
    }
}
