package net.pl3x.map.world;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.pl3x.map.api.Pl3xMap;
import net.pl3x.map.api.event.world.WorldLoadedEvent;
import net.pl3x.map.api.event.world.WorldUnloadedEvent;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.logger.Logger;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @NotNull
    public Collection<MapWorld> getMapWorlds() {
        return Collections.unmodifiableCollection(this.mapWorlds.values());
    }

    /**
     * Get map world if enabled
     *
     * @param world Bukkit world
     * @return map world
     */
    @Nullable
    public MapWorld getMapWorld(@Nullable World world) {
        return world == null ? null : this.mapWorlds.get(world.getUID());
    }

    /**
     * Load a map world
     *
     * @param world Bukkit world
     */
    public void loadWorld(@NotNull World world) {
        if (this.mapWorlds.containsKey(world.getUID())) {
            throw new RuntimeException("World is already loaded");
        }
        WorldConfig config = new WorldConfig(world);
        try {
            config.reload();
        } catch (RuntimeException ignore) {
            Logger.debug("<yellow>Skipping <world>"
                    .replace("<world>", world.getName()));
            return;
        }
        MapWorld mapWorld = new MapWorld(world, config);
        this.mapWorlds.put(world.getUID(), mapWorld);
        Pl3xMap.api().getPaletteManager().register(mapWorld);
        new WorldLoadedEvent(mapWorld).callEvent();
        Logger.debug("<green>Loaded <world>"
                .replace("<world>", world.getName()));
    }

    /**
     * Unload a map world
     *
     * @param world Bukkit world
     */
    public void unloadWorld(@NotNull World world) {
        MapWorld mapWorld = this.mapWorlds.remove(world.getUID());
        if (mapWorld != null) {
            mapWorld.unload();
            new WorldUnloadedEvent(mapWorld).callEvent();
        }
    }

    public void unload() {
        new HashSet<>(this.mapWorlds.values()).forEach(mapWorld -> unloadWorld(mapWorld.getWorld()));
    }
}
