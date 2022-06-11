package net.pl3x.map.world;

import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.renderer.AbstractRenderer;
import org.bukkit.World;

import java.util.UUID;

/**
 * Represents a world which is mapped by Pl3xMap
 */
public class MapWorld {
    private final World world;
    private final WorldConfig config;

    private AbstractRenderer renderer;

    /**
     * Constructs a MapWorld for given world
     */
    public MapWorld(World world, WorldConfig config) {
        this.world = world;
        this.config = config;
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

    public WorldConfig getConfig() {
        return this.config;
    }

    public AbstractRenderer getRenderer() {
        return this.renderer;
    }

    public void unload() {
        this.renderer.stop();
    }
}
