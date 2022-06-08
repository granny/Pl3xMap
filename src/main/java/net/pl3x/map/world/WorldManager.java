package net.pl3x.map.world;

import org.bukkit.World;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages mapped worlds
 */
public class WorldManager {
    private final Map<UUID, MapWorld> mapWorlds = new ConcurrentHashMap<>();

    /**
     * Get all the loaded map worlds
     *
     * @return map worlds
     */
    public Collection<MapWorld> getMapWorlds() {
        return Collections.unmodifiableCollection(this.mapWorlds.values());
    }

    /**
     * Get map world if enabled
     *
     * @param world Bukkit world
     * @return map world
     */
    public MapWorld getMapWorld(World world) {
        return world == null ? null : this.mapWorlds.get(world.getUID());
    }

    /**
     * Load a map world
     *
     * @param world Bukkit world
     * @return map world
     */
    public MapWorld loadWorld(World world) {
        return this.mapWorlds.computeIfAbsent(world.getUID(), uuid -> new MapWorld(world));
    }

    /**
     * Unload a map world
     *
     * @param world Bukkit world
     */
    public void unloadWorld(World world) {
        this.mapWorlds.remove(world.getUID());
    }
}
