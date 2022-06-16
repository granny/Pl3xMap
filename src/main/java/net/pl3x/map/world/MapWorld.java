package net.pl3x.map.world;

import net.minecraft.server.level.ServerLevel;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.util.FileUtil;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import java.nio.file.Path;
import java.util.UUID;

/**
 * Represents a world which is mapped by Pl3xMap
 */
public class MapWorld {
    public static final Path WEB_DIR = FileUtil.PLUGIN_DIR.resolve(Config.WEB_DIR);
    public static final Path TILES_DIR = WEB_DIR.resolve("tiles");

    private final World world;
    private final ServerLevel level;
    private final WorldConfig config;

    private AbstractRender activeRender = null;

    /**
     * Constructs a MapWorld for given world
     */
    public MapWorld(World world, WorldConfig config) {
        this.world = world;
        this.level = ((CraftWorld) world).getHandle();
        this.config = config;
    }

    /**
     * Get the bukkit world instance
     *
     * @return bukkit world
     */
    public World getWorld() {
        return this.world;
    }

    /**
     * Get the level instance
     *
     * @return level
     */
    public ServerLevel getLevel() {
        return this.level;
    }

    /**
     * Get the name of this world
     *
     * @return world name
     */
    public String getName() {
        return this.world.getName();
    }

    /**
     * Get the UUID of this world
     *
     * @return world uuid
     */
    public UUID getUUID() {
        return this.world.getUID();
    }

    public WorldConfig getConfig() {
        return this.config;
    }

    /**
     * Check if a render is currently in progress on this world
     *
     * @return true if a render is in progress
     */
    public boolean isRendering() {
        return this.activeRender != null;
    }

    public void stopRender() {
        if (!isRendering()) {
            throw new IllegalStateException("No render to stop");
        }

        this.activeRender.cancel();
        this.activeRender = null;
    }

    public void startRender(AbstractRender render) {
        if (isRendering()) {
            throw new IllegalStateException("Already rendering");
        }

        this.activeRender = render;
        this.activeRender.runTaskAsynchronously(Pl3xMap.getInstance());
    }

    public void unload() {
        if (isRendering()) {
            stopRender();
        }
    }
}
