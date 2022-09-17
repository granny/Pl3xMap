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

    /**
     * Create a new players layer.
     *
     * @param world world
     */
    public PlayersLayer(@NotNull World world) {
        this(KEY, world, () -> Lang.UI_LAYER_PLAYERS);
        setUpdateInterval(0);
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

        Path player = World.WEB_DIR.resolve("images/icon/player.png");
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
        Icon icon = Marker.icon(player.getKey(), player.getPosition(), KEY, 16)
                .setRotationAngle((double) player.getYaw())
                .setRotationOrigin("center");
        icon.setOptions(Options.builder()
                .tooltipContent("""
                                <ul>
                                <li><img src='images/skins/2D/<uuid>.png' class='head' /></li>
                                <li><name>
                                <img src='images/clear.png' class='health' style='background-position:0 -<health>px;' />
                                <img src='images/clear.png' class='armor' style='background-position:0 -<armor>px;' /></li>
                                </ul>"""
                                .replace("<uuid>", player.getUUID().toString())
                                .replace("<name>", player.getName())
                                .replace("<health>", Integer.toString(player.getHealth() * 9))
                                .replace("<armor>", Integer.toString(player.getArmorPoints() * 9))
                        // width:189px;height:9px;background:url('images/armor.png') no-repeat;background-position:0 -36px;
                )
                .tooltipPane("nameplates")
                .tooltipDirection(Tooltip.Direction.RIGHT)
                .tooltipPermanent(true)
                .tooltipOffset(Point.of(5, 0))
                .tooltipOpacity(1.0D)
                .build()
        );
        return icon;
    }
}
