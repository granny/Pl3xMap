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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.coordinate.ChunkCoordinate;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.event.world.WorldLoadedEvent;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.markers.layer.LayerRegistry;
import net.pl3x.map.render.job.BackgroundRender;
import net.pl3x.map.render.job.FullRender;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.task.UpdateMarkerData;
import net.pl3x.map.util.FileUtil;

/**
 * Represents a world which is mapped by Pl3xMap
 */
public class MapWorld {
    public static final Path WEB_DIR = Config.WEB_DIR.startsWith("/") ? Path.of(Config.WEB_DIR) : FileUtil.MAIN_DIR.resolve(Config.WEB_DIR);
    public static final Path TILES_DIR = WEB_DIR.resolve("tiles");

    private static final String DIRTY_CHUNKS = "dirty_chunks.json";
    private static final String SCANNED_REGIONS = "resume_render.json";
    private static final Gson GSON = new GsonBuilder().enableComplexMapKeySerialization().create();

    private final World world;
    private final WorldConfig config;
    private final LayerRegistry layerRegistry;

    private final Path dataPath;
    private final Path tilesPath;

    private final ScheduledExecutorService backgroundExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Background").build());
    private final ScheduledExecutorService markersExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Markers").build());

    private ScheduledFuture<?> backgroundRender;
    private ScheduledFuture<?> markersUpdater;

    private Render activeRender = null;

    private final Set<RegionCoordinate> modifiedRegions = ConcurrentHashMap.newKeySet();
    private final LinkedHashMap<RegionCoordinate, Boolean> scannedRegions = new LinkedHashMap<>();

    private boolean paused;

    /**
     * Constructs a MapWorld for given world
     */
    public MapWorld(World world, WorldConfig config) {
        this.world = world;
        this.config = config;
        this.layerRegistry = new LayerRegistry();

        String dirName = getWorld().getName().replace(":", "-");
        this.dataPath = FileUtil.DATA_DIR.resolve(dirName);
        this.tilesPath = TILES_DIR.resolve(dirName);

    }

    void init() {
        try {
            if (!Files.exists(this.dataPath)) {
                Files.createDirectories(this.dataPath);
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to create data directory for world '%s'", getWorld().getName()), e);
        }

        try {
            if (!Files.exists(this.tilesPath)) {
                Files.createDirectories(this.tilesPath);
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to create tiles directory for world '%s'", getWorld().getName()), e);
        }

        startMarkersTask();
        startBackgroundRender();

        deserializeDirtyChunks();
        deserializeScannedRegions();

        if (!getScannedRegions().isEmpty()) {
            startRender(new FullRender(this, Pl3xMap.api().getConsole()));
        }

        Pl3xMap.api().getPaletteRegistry().register(this);
        new WorldLoadedEvent(this).callEvent();
        Logger.debug("<green>Loaded <world>"
                .replace("<world>", getWorld().getName()));
    }

    /**
     * Get the minecraft world
     *
     * @return world
     */
    public World getWorld() {
        return this.world;
    }

    public WorldConfig getConfig() {
        return this.config;
    }

    public LayerRegistry getLayerRegistry() {
        return this.layerRegistry;
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
            Logger.warn(String.format("Failed to deserialize render progress for world '%s'", getWorld().getName()));
            e.printStackTrace();
        }
    }

    public void serializeScannedRegions() {
        try {
            Files.writeString(this.dataPath.resolve(SCANNED_REGIONS), GSON.toJson(getScannedRegions()));
        } catch (IOException e) {
            Logger.warn(String.format("Failed to serialize render progress for world '%s'", getWorld().getName()));
            e.printStackTrace();
        }
    }

    private void deserializeDirtyChunks() {
        try {
            final Path file = this.dataPath.resolve(DIRTY_CHUNKS);
            if (Files.exists(file)) {
                this.modifiedRegions.addAll(GSON.fromJson(
                        new FileReader(file.toFile()),
                        TypeToken.getParameterized(List.class, ChunkCoordinate.class).getType()
                ));
            }
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            Logger.warn(String.format("Failed to deserialize dirty chunks for world '%s'", getWorld().getName()));
            e.printStackTrace();
        }
    }

    private void serializeDirtyChunks() {
        try {
            Files.writeString(this.dataPath.resolve(DIRTY_CHUNKS), GSON.toJson(this.modifiedRegions));
        } catch (IOException e) {
            Logger.warn(String.format("Failed to serialize dirty chunks for world '%s'", getWorld().getName()));
            e.printStackTrace();
        }
    }

    public void addModifiedRegion(RegionCoordinate region) {
        this.modifiedRegions.add(region);
    }

    public boolean hasModifiedRegions() {
        return !this.modifiedRegions.isEmpty();
    }

    public RegionCoordinate getNextModifiedRegion() {
        if (!hasModifiedRegions()) {
            return null;
        }
        Iterator<RegionCoordinate> it = this.modifiedRegions.iterator();
        RegionCoordinate region = it.next();
        it.remove();
        return region;
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

        this.backgroundRender = this.backgroundExecutor.scheduleAtFixedRate(new BackgroundRender(this), interval, interval, TimeUnit.SECONDS);
    }

    public void stopBackgroundRender() {
        if (!hasBackgroundRender()) {
            throw new IllegalStateException("Not background rendering");
        }

        this.backgroundRender.cancel(false);
        this.backgroundRender = null;
    }

    public void startMarkersTask() {
        this.markersUpdater = this.markersExecutor.scheduleAtFixedRate(new UpdateMarkerData(this), ThreadLocalRandom.current().nextInt(1000), 1000, TimeUnit.MILLISECONDS);
    }

    public void stopMarkersTask() {
        this.markersUpdater.cancel(false);
        this.markersUpdater = null;
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
        this.backgroundExecutor.submit(render);
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

        stopMarkersTask();

        serializeDirtyChunks();
        serializeScannedRegions();

        Pl3xMap.api().getPaletteRegistry().unregister(this);
    }

    /**
     * Get the tiles directory for this world
     *
     * @return world tiles directory
     */
    public Path getTilesDir() {
        return this.tilesPath;
    }

    /**
     * Get the markers directory for this world
     *
     * @return world markers directory
     */
    public Path getMarkersDir() {
        return getTilesDir().resolve("markers");
    }
}
