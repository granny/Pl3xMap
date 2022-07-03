package net.pl3x.map.world;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.BiomeManager;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.render.job.BackgroundRender;
import net.pl3x.map.render.job.FullRender;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.render.job.iterator.coordinate.ChunkCoordinate;
import net.pl3x.map.render.job.iterator.coordinate.RegionCoordinate;
import net.pl3x.map.util.BiomeColors;
import net.pl3x.map.util.FileUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_19_R1.CraftWorld;

/**
 * Represents a world which is mapped by Pl3xMap
 */
public class MapWorld {
    public static final Path WEB_DIR = FileUtil.PLUGIN_DIR.resolve(Config.WEB_DIR);
    public static final Path TILES_DIR = WEB_DIR.resolve("tiles");

    private static final String DIRTY_CHUNKS = "dirty_chunks.json";
    private static final String SCANNED_REGIONS = "resume_render.json";
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    private final World world;
    private final ServerLevel level;
    private final WorldConfig config;

    private final Path dataPath;

    private final BiomeColors biomeColors;
    private final long biomeSeed;

    private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1,
            new ThreadFactoryBuilder().setNameFormat("Pl3xMap").build());

    private ScheduledFuture<?> backgroundRender;
    private Render activeRender = null;

    private final Set<ChunkCoordinate> modifiedChunks = ConcurrentHashMap.newKeySet();
    private final LinkedHashMap<RegionCoordinate, Boolean> scannedRegions = new LinkedHashMap<>();

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

        this.dataPath = FileUtil.DATA_DIR.resolve(world.getName());
        try {
            if (!Files.exists(this.dataPath)) {
                Files.createDirectories(this.dataPath);
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to create data directory for world '%s'", getName()), e);
        }

        startBackgroundRender();

        deserializeDirtyChunks();
        deserializeScannedRegions();

        if (!getScannedRegions().isEmpty()) {
            startRender(new FullRender(this, Bukkit.getConsoleSender()));
        }
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

    public void deserializeScannedRegions() {
        try {
            final Path file = this.dataPath.resolve(SCANNED_REGIONS);
            if (Files.exists(file)) {
                String json = String.join("", Files.readAllLines(file));
                TypeToken<LinkedHashMap<RegionCoordinate, Boolean>> token = new TypeToken<>() {
                };
                setScannedRegions(GSON.fromJson(json, token.getType()));
            }
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            Logger.warn(String.format("Failed to deserialize render progress for world '%s'", getName()));
            e.printStackTrace();
        }
    }

    public void serializeScannedRegions() {
        try {
            Files.writeString(this.dataPath.resolve(SCANNED_REGIONS), GSON.toJson(getScannedRegions()));
        } catch (IOException e) {
            Logger.warn(String.format("Failed to serialize render progress for world '%s'", getName()));
            e.printStackTrace();
        }
    }

    private void deserializeDirtyChunks() {
        try {
            final Path file = this.dataPath.resolve(DIRTY_CHUNKS);
            if (Files.exists(file)) {
                this.modifiedChunks.addAll(GSON.fromJson(
                        new FileReader(file.toFile()),
                        TypeToken.getParameterized(List.class, ChunkCoordinate.class).getType()
                ));
            }
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            Logger.warn(String.format("Failed to deserialize dirty chunks for world '%s'", getName()));
            e.printStackTrace();
        }
    }

    private void serializeDirtyChunks() {
        try {
            Files.writeString(this.dataPath.resolve(DIRTY_CHUNKS), GSON.toJson(this.modifiedChunks));
        } catch (IOException e) {
            Logger.warn(String.format("Failed to serialize dirty chunks for world '%s'", getName()));
            e.printStackTrace();
        }
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

    public void clearScannedRegions() {
        synchronized (this.scannedRegions) {
            this.scannedRegions.clear();
        }
    }

    public void setScannedRegions(LinkedHashMap<RegionCoordinate, Boolean> scannedRegions) {
        synchronized (this.scannedRegions) {
            this.scannedRegions.clear();
            this.scannedRegions.putAll(scannedRegions);
        }
    }

    public void setScannedRegion(RegionCoordinate region) {
        synchronized (this.scannedRegions) {
            if (this.scannedRegions.containsKey(region)) {
                this.scannedRegions.put(region, true);
            }
        }
    }

    public LinkedHashMap<RegionCoordinate, Boolean> getScannedRegions() {
        synchronized (this.scannedRegions) {
            return this.scannedRegions;
        }
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

    public Render getActiveRender() {
        return this.activeRender;
    }

    public void startRender(Render render) {
        if (hasActiveRender()) {
            throw new IllegalStateException("Already rendering");
        }

        stopBackgroundRender();

        this.activeRender = render;
        this.executor.submit(render);
    }

    public void cancelRender(boolean unloading) {
        if (!hasActiveRender()) {
            throw new IllegalStateException("No render to cancel");
        }

        this.activeRender.cancel(unloading);
        this.activeRender = null;

        if (!unloading) {
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
            cancelRender(true);
        }

        serializeDirtyChunks();
        serializeScannedRegions();
    }
}
