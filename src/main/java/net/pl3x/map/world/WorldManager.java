package net.pl3x.map.world;

import net.pl3x.map.configuration.WorldConfig;
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
        if (mapWorlds.containsKey(world.getUID())) {
            throw new RuntimeException("World is already loaded");
        }
        WorldConfig config = new WorldConfig(world);
        config.reload();
        MapWorld mapWorld = new MapWorld(world, config);
        mapWorlds.put(world.getUID(), mapWorld);
        return mapWorld;
    }

    /**
     * Unload a map world
     *
     * @param world Bukkit world
     */
    public void unloadWorld(World world) {
        MapWorld mapWorld = this.mapWorlds.remove(world.getUID());
        if (mapWorld != null) {
            mapWorld.unload();
        }
    }
}
