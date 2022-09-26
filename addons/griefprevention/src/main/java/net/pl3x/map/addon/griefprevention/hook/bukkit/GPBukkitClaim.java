package net.pl3x.map.addon.griefprevention.hook.bukkit;

import java.util.ArrayList;
import me.ryanhamshire.GriefPrevention.Claim;
import net.pl3x.map.addon.griefprevention.hook.GPClaim;
import net.pl3x.map.markers.Point;
import net.pl3x.map.world.World;
import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class GPBukkitClaim implements GPClaim {
    private final World world;
    private final Claim claim;
    private final Point min;
    private final Point max;

    public GPBukkitClaim(@NotNull World world, @NotNull Claim claim) {
        this.world = world;
        this.claim = claim;

        Location min = this.claim.getLesserBoundaryCorner();
        Location max = this.claim.getGreaterBoundaryCorner();
        this.min = Point.of(min.getX(), min.getZ());
        this.max = Point.of(max.getX(), max.getZ());
    }

    @Override
    @NotNull
    public World getWorld() {
        return this.world;
    }

    @Override
    public boolean isAdminClaim() {
        return this.claim.isAdminClaim();
    }

    @Override
    @NotNull
    public Long getID() {
        return this.claim.getID();
    }

    @Override
    @NotNull
    public String getOwnerName() {
        return this.claim.getOwnerName();
    }

    @Override
    @NotNull
    public Point getMin() {
        return this.min;
    }

    @Override
    @NotNull
    public Point getMax() {
        return this.max;
    }

    @Override
    public int getArea() {
        return this.claim.getArea();
    }

    @Override
    public int getWidth() {
        return this.claim.getWidth();
    }

    @Override
    public int getHeight() {
        return this.claim.getHeight();
    }

    @Override
    public void getPermissions(
            @NotNull ArrayList<String> builders,
            @NotNull ArrayList<String> containers,
            @NotNull ArrayList<String> accessors,
            @NotNull ArrayList<String> managers
    ) {
        this.claim.getPermissions(builders, containers, accessors, managers);
    }
}
