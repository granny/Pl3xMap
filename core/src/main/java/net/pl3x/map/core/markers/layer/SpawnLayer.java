package net.pl3x.map.core.markers.layer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import javax.imageio.ImageIO;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.image.IconImage;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.markers.option.Tooltip;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Manages world spawn marker.
 */
public class SpawnLayer extends WorldLayer {
    public static final String KEY = "spawn";

    private final Collection<Marker<?>> markers;

    /**
     * Create a new spawn layer.
     *
     * @param world world
     */
    public SpawnLayer(@NonNull World world) {
        super(KEY, world, () -> Lang.UI_LAYER_SPAWN);

        Path icon = FileUtil.getWebDir().resolve("images/icon/" + KEY + ".png");
        try {
            IconImage image = new IconImage(KEY, ImageIO.read(icon.toFile()), "png");
            Pl3xMap.api().getIconRegistry().register(image);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setUpdateInterval(15);

        this.markers = Collections.singletonList(
                Marker.icon(KEY, getWorld().getSpawn(), KEY, 16)
                        .setOptions(Options.builder()
                                .tooltipContent(getLabel())
                                .tooltipDirection(Tooltip.Direction.TOP)
                                .build()
                        )
        );
    }

    @Override
    @NonNull
    public Collection<Marker<?>> getMarkers() {
        return this.markers;
    }
}
