package net.pl3x.map.world;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.render.AbstractRender;
import net.pl3x.map.render.BackgroundRender;
import net.pl3x.map.render.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.FileUtil;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

import java.nio.file.Path;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

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

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("Pl3xMap").build());

    private ScheduledFuture<?> backgroundRender;
    private AbstractRender activeRender = null;

    private final Set<ChunkCoordinate> modifiedChunks = ConcurrentHashMap.newKeySet();

    public long highestInhabitedTime = 0;

    private boolean paused;

    /**
     * Constructs a MapWorld for given world
     */
    public MapWorld(World world, WorldConfig config) {
        this.world = world;
        this.level = ((CraftWorld) world).getHandle();
        this.config = config;

        this.biomeColors = new BiomeColors(this);
        this.biomeSeed = BiomeManager.obfuscateSeed(this.level.getSeed());

        startBackgroundRender();
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

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public void addModifiedChunk(ChunkCoordinate chunk) {
        this.modifiedChunks.add(chunk);
    }

    public boolean hasModifiedChunks() {
        return !this.modifiedChunks.isEmpty();
    }

    public ChunkCoordinate getNextModifiedChunk() {
        if (!hasModifiedChunks()) {
            return null;
        }
        Iterator<ChunkCoordinate> it = this.modifiedChunks.iterator();
        ChunkCoordinate chunk = it.next();
        it.remove();
        return chunk;
    }

    public boolean hasBackgroundRender() {
        return getBackgroundRender() != null;
    }

    public ScheduledFuture<?> getBackgroundRender() {
        return this.backgroundRender;
    }

    public void startBackgroundRender() {
        if (hasBackgroundRender() || hasActiveRender()) {
            throw new IllegalStateException("Already rendering");
        }

        int interval = getConfig().RENDER_BACKGROUND_INTERVAL;

        if (interval < 1) {
            return;
        }

        this.backgroundRender = this.executor.scheduleAtFixedRate(new BackgroundRender(this), interval, interval, TimeUnit.SECONDS);
    }

    public void stopBackgroundRender() {
        if (!hasBackgroundRender()) {
            throw new IllegalStateException("Not background rendering");
        }

        this.backgroundRender.cancel(false);
        this.backgroundRender = null;
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

        stopBackgroundRender();

        this.activeRender = render;
        this.executor.submit(render);
    }

    public void cancelRender(boolean startBackgroundRender) {
        if (!hasActiveRender()) {
            throw new IllegalStateException("No render to cancel");
        }

        this.activeRender.cancel();
        this.activeRender = null;

        if (startBackgroundRender) {
            startBackgroundRender();
        }
    }

    public void finishRender() {
        if (!hasActiveRender()) {
            throw new IllegalStateException("No render to finish");
        }

        this.activeRender.finish();
        this.activeRender = null;

        startBackgroundRender();
    }

    public void unload() {
        if (hasActiveRender()) {
            cancelRender(false);
        }
    }
}
