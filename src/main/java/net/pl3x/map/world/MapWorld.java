package net.pl3x.map.world;

import org.bukkit.World;

import java.util.UUID;

/**
 * Represents a world which is mapped by Pl3xMap
 */
public class MapWorld {
    private final World world;

    /**
     * Constructs a MapWorld for given world
     */
    public MapWorld(World world) {
        this.world = world;
    }

    /**
     * Get the bukkit world instance
     *
     * @return bukkit world
     */
    public World getWorld() {
        return world;
    }

    /**
     * Get the name of this world
     *
     * @return world name
     */
    public String getName() {
        return world.getName();
    }

    /**
     * Get the UUID of this world
     *
     * @return world uuid
     */
    public UUID getUUID() {
        return world.getUID();
    }

    /**
     * Check if a render is currently in progress on this world
     *
     * @return true if a render is in progress
     */
    public boolean isRendering() {
        return false; // TODO
    }
}
