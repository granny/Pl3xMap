package net.pl3x.map.world;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.render.task.AbstractRender;
import net.pl3x.map.util.BiomeColors;
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

    private final BiomeColors biomeColors;
    private final long biomeSeed;

    private AbstractRender activeRender = null;

    /**
     * Constructs a MapWorld for given world
     */
    public MapWorld(World world, WorldConfig config) {
        this.world = world;
        this.level = ((CraftWorld) world).getHandle();
        this.config = config;

        this.biomeColors = new BiomeColors(this.level);
        this.biomeSeed = BiomeManager.obfuscateSeed(this.level.getSeed());
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

    public BiomeColors getBiomeColors() {
        return this.biomeColors;
    }

    public long getBiomeSeed() {
        return this.biomeSeed;
    }

    /**
     * Check if a render is currently in progress on this world
     *
     * @return true if a render is in progress
     */
    public boolean hasActiveRender() {
        return getActiveRender() != null;
    }

    public AbstractRender getActiveRender() {
        return this.activeRender;
    }

    public void startRender(AbstractRender render) {
        if (hasActiveRender()) {
            throw new IllegalStateException("Already rendering");
        }
        this.activeRender = render;
        this.activeRender.runTaskAsynchronously(Pl3xMap.getInstance());
    }

    public void cancelRender() {
        if (!hasActiveRender()) {
            throw new IllegalStateException("No render to cancel");
        }
        this.activeRender.cancel();
        this.activeRender = null;
    }

    public void finishRender() {
        if (!hasActiveRender()) {
            throw new IllegalStateException("No render to finish");
        }
        this.activeRender = null;
    }

    public void unload() {
        if (hasActiveRender()) {
            cancelRender();
        }
    }
}
