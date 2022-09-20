package net.pl3x.map.markers.layer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;
import javax.imageio.ImageIO;
import net.pl3x.map.Key;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.configuration.PlayerTracker;
import net.pl3x.map.image.IconImage;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.marker.Icon;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.markers.option.Tooltip;
import net.pl3x.map.player.Player;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

/**
 * Manages player markers.
 */
public class PlayersLayer extends WorldLayer {
    public static final Key KEY = Key.of("players");

    private final Key icon;

    /**
     * Create a new players layer.
     *
     * @param world world
     */
    public PlayersLayer(@NotNull World world) {
        this(KEY, world, () -> Lang.UI_LAYER_PLAYERS);
        setUpdateInterval(0);
        setPane(PlayerTracker.PANE);
        setCss(PlayerTracker.CSS);
    }

    /**
     * Create a new players layer.
     *
     * @param key           key for layer
     * @param world         world
     * @param labelSupplier label
     */
    public PlayersLayer(@NotNull Key key, @NotNull World world, @NotNull Supplier<String> labelSupplier) {
        super(key, world, labelSupplier);

        this.icon = Key.of(PlayerTracker.ICON);

        Path player = World.WEB_DIR.resolve("images/icon/" + this.icon + ".png");
        try {
            IconImage playerImage = new IconImage(key, ImageIO.read(player.toFile()), "png");
            Pl3xMap.api().getIconRegistry().register(playerImage);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @NotNull
    public Collection<Marker<?>> getMarkers() {
        Set<Marker<?>> icons = new HashSet<>();
        this.getWorld().getPlayers().forEach(player -> icons.add(createIcon(player)));
        return icons;
    }

    private Icon createIcon(Player player) {
        Icon icon = Marker.icon(player.getKey(), player.getPosition(), this.icon, 16)
                .setRotationAngle((double) player.getYaw())
                .setRotationOrigin("center")
                .setPane("players");
        String tooltip = PlayerTracker.TOOLTIP;
        if (tooltip == null || tooltip.isBlank()) {
            return icon;
        }
        return icon.setOptions(Options.builder()
                .tooltipContent(tooltip
                        .replace("<uuid>", player.getUUID().toString())
                        .replace("<name>", player.getName())
                        .replace("<decoratedName>", player.getDecoratedName())
                        .replace("<health>", Integer.toString(player.getHealth()))
                        .replace("<armor>", Integer.toString(player.getArmorPoints()))
                )
                .tooltipPane(PlayerTracker.PANE)
                .tooltipDirection(Tooltip.Direction.RIGHT)
                .tooltipPermanent(true)
                .tooltipOffset(Point.of(5, 0))
                .tooltipOpacity(1.0D)
                .build()
        );
    }
}
