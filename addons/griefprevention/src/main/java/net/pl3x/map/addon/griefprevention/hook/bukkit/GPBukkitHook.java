package net.pl3x.map.addon.griefprevention.hook.bukkit;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import net.pl3x.map.Key;
import net.pl3x.map.addon.griefprevention.hook.GPHook;
import net.pl3x.map.markers.marker.Marker;
import net.pl3x.map.world.World;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class GPBukkitHook implements GPHook {
    public static final List<Marker<?>> EMPTY_LIST = new ArrayList<>();

    @Override
    public boolean isGPReady() {
        return GriefPrevention.instance != null;
    }

    @Override
    public boolean isWorldEnabled(@NotNull String name) {
        return isGPReady() && GriefPrevention.instance.claimsEnabledForWorld(Bukkit.getWorld(name));
    }

    @Override
    @NotNull
    public Collection<Marker<?>> getClaims(@NotNull World world) {
        if (!isWorldEnabled(world.getName())) {
            return EMPTY_LIST;
        }
        return GriefPrevention.instance.dataStore.getClaims().stream()
                .filter(claim -> claim.getLesserBoundaryCorner() != null)
                .filter(claim -> claim.getLesserBoundaryCorner().getWorld().getName().equals(world.getName()))
                .map(claim -> new GPBukkitClaim(world, claim))
                .map(claim -> {
                    Key key = Key.of("gp-claim-" + claim.getID());
                    return Marker.rectangle(key, claim.getMin(), claim.getMax())
                            .setOptions(getOptions(claim));
                })
                .collect(Collectors.toSet());
    }

    @Override
    @NotNull
    public String getNames(@NotNull List<String> list) {
        List<String> names = new ArrayList<>();
        for (String str : list) {
            try {
                UUID uuid = UUID.fromString(str);
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                names.add(offlinePlayer.getName());
            } catch (Exception e) {
                names.add(str);
            }
        }
        return String.join(", ", names);
    }
}
