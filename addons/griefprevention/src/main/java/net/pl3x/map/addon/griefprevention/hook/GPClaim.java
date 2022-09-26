package net.pl3x.map.addon.griefprevention.hook;

import java.util.ArrayList;
import net.pl3x.map.markers.Point;
import net.pl3x.map.world.World;
import org.jetbrains.annotations.NotNull;

public interface GPClaim {
    @NotNull
    World getWorld();

    boolean isAdminClaim();

    @NotNull
    Long getID();

    @NotNull
    CharSequence getOwnerName();

    @NotNull
    Point getMin();

    @NotNull
    Point getMax();

    int getArea();

    int getWidth();

    int getHeight();

    void getPermissions(ArrayList<String> builders, ArrayList<String> containers, ArrayList<String> accessors, ArrayList<String> managers);
}
