package net.pl3x.map.addon.griefprevention.hook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.ryanhamshire.GriefPrevention.Claim;
import net.pl3x.map.addon.griefprevention.configuration.Config;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.markers.option.Options;
import net.pl3x.map.util.Colors;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public interface GPHook {
    boolean isWorldEnabled(@NotNull String name);

    @NotNull
    Collection<Marker<?>> getClaims(@NotNull World world);

    @NotNull
    default Options getOptions(@NotNull Claim claim, @NotNull String worldName) {
        Options.Builder builder;
        if (claim.isAdminClaim()) {
            builder = Options.builder()
                    .strokeWeight(Config.MARKER_ADMIN_STROKE_WEIGHT)
                    .strokeColor(Colors.fromHex(Config.MARKER_ADMIN_STROKE_COLOR))
                    .fillColor(Colors.fromHex(Config.MARKER_ADMIN_FILL_COLOR))
                    .popupContent(processPopup(Config.MARKER_ADMIN_POPUP, claim, worldName));
        } else {
            builder = Options.builder()
                    .strokeWeight(Config.MARKER_BASIC_STROKE_WEIGHT)
                    .strokeColor(Colors.fromHex(Config.MARKER_BASIC_STROKE_COLOR))
                    .fillColor(Colors.fromHex(Config.MARKER_BASIC_FILL_COLOR))
                    .popupContent(processPopup(Config.MARKER_BASIC_POPUP, claim, worldName));
        }
        return builder.build();
    }

    @NotNull
    default String processPopup(@NotNull String popup, @NotNull Claim claim, @NotNull String worldName) {
        ArrayList<String> builders = new ArrayList<>();
        ArrayList<String> containers = new ArrayList<>();
        ArrayList<String> accessors = new ArrayList<>();
        ArrayList<String> managers = new ArrayList<>();
        claim.getPermissions(builders, containers, accessors, managers);
        return popup.replace("<world>", worldName)
                .replace("<id>", Long.toString(claim.getID()))
                .replace("<owner>", claim.getOwnerName())
                .replace("<managers>", getNames(managers))
                .replace("<builders>", getNames(builders))
                .replace("<containers>", getNames(containers))
                .replace("<accessors>", getNames(accessors))
                .replace("<area>", Integer.toString(claim.getArea()))
                .replace("<width>", Integer.toString(claim.getWidth()))
                .replace("<height>", Integer.toString(claim.getHeight()));
    }

    @NotNull
    String getNames(@NotNull List<String> list);
}
