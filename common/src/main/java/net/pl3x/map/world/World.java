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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import javax.imageio.ImageIO;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.pl3x.map.Key;
import net.pl3x.map.Keyed;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.PlayerTracker;
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.event.world.WorldLoadedEvent;
import net.pl3x.map.image.IconImage;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.markers.Point;
import net.pl3x.map.markers.layer.Layer;
import net.pl3x.map.markers.layer.PlayersLayer;
import net.pl3x.map.markers.layer.SpawnLayer;
import net.pl3x.map.markers.layer.WorldBorderLayer;
import net.pl3x.map.palette.BiomePaletteRegistry;
import net.pl3x.map.palette.Palette;
import net.pl3x.map.palette.PaletteRegistry;
import net.pl3x.map.player.Player;
import net.pl3x.map.registry.KeyedRegistry;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.job.BackgroundRender;
import net.pl3x.map.render.job.FullRender;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.task.UpdateMarkerData;
import net.pl3x.map.util.FileUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a minecraft world.
 */
public abstract class World extends Keyed {
    public static final Path WEB_DIR = Config.WEB_DIR.startsWith("/") ? Path.of(Config.WEB_DIR) : FileUtil.MAIN_DIR.resolve(Config.WEB_DIR);
    public static final Path TILES_DIR = WEB_DIR.resolve("tiles");

    private static final String DIRTY_REGIONS = "dirty_regions.json";
    private static final String SCANNED_REGIONS = "resume_render.json";

    private static final Gson GSON = new GsonBuilder()
            .enableComplexMapKeySerialization()
            //.setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    private final ServerLevel level;
    private final Type type;
    private final long seed;

    private final WorldConfig config;

    private BiomePaletteRegistry biomePaletteRegistry;
    private final Registry<Biome> biomeRegistry;
    private final KeyedRegistry<Layer> layerRegistry;

    private final Path dataPath;
    private final Path tilesPath;

    private final ScheduledExecutorService backgroundExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Background").build());
    private final ScheduledExecutorService markersExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Markers").build());

    private ScheduledFuture<?> backgroundRender;
    private ScheduledFuture<?> markersUpdater;

    private final Map<Key, RendererHolder> rendererHolders = new LinkedHashMap<>();

    private Render activeRender = null;

    private final ConcurrentLinkedQueue<RegionCoordinate> modifiedRegions = new ConcurrentLinkedQueue<>();
    private final LinkedHashMap<RegionCoordinate, Boolean> scannedRegions = new LinkedHashMap<>();

    private boolean alreadyInitialized;
    private boolean paused;

    /**
     * Create a new world.
     *
     * @param key   identifying key
     * @param level minecraft server level
     */
    public World(@NotNull Key key, @NotNull ServerLevel level) {
        super(key);
        this.level = level;
        this.type = Type.get(level);
        this.seed = BiomeManager.obfuscateSeed(level.getSeed());

        this.config = new WorldConfig(this);

        this.biomePaletteRegistry = new BiomePaletteRegistry();
        this.biomeRegistry = level.registryAccess().ownedRegistryOrThrow(Registry.BIOME_REGISTRY);
        this.layerRegistry = new KeyedRegistry<>();

        String dirName = getName().replace(":", "-");
        this.dataPath = FileUtil.DATA_DIR.resolve(dirName);
        this.tilesPath = TILES_DIR.resolve(dirName);
    }

    /**
     * Create a new key.
     *
     * @param name world name
     * @return a new key
     */
    @NotNull
    public static Key createKey(@NotNull String name) {
        return Key.of(name);
    }

    /**
     * Initialize this world.
     * <p>
     * This automatically happens when the world is registered.
     * <p>
     * Calling this method on an initialized world with throw a {@link IllegalStateException}.
     */
    public void init() {
        if (this.alreadyInitialized) {
            throw new IllegalStateException("World already initialized!");
        }
        this.alreadyInitialized = true;

        getConfig().reload();

        if (!isEnabled()) {
            return;
        }

        getConfig().RENDER_RENDERERS.forEach((rendererName, icon) -> {
            RendererHolder holder = Pl3xMap.api().getRendererRegistry().get(rendererName);
            if (holder == null) {
                return;
            }
            Key iconKey = Key.of(icon);
            Path path = World.WEB_DIR.resolve("images/icon/" + iconKey + ".png");
            try {
                IconImage image = new IconImage(iconKey, ImageIO.read(path.toFile()), "png");
                Pl3xMap.api().getIconRegistry().register(image);
                rendererHolders.put(holder.getKey(), holder);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        try {
            if (!Files.exists(this.dataPath)) {
                Files.createDirectories(this.dataPath);
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to create data directory for world '%s'", getName()), e);
        }

        try {
            if (!Files.exists(this.tilesPath)) {
                Files.createDirectories(this.tilesPath);
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Failed to create tiles directory for world '%s'", getName()), e);
        }

        rebuildBiomesPaletteRegistry();

        if (getConfig().MARKERS_WORLDBORDER_ENABLED) {
            getLayerRegistry().register(new WorldBorderLayer(this));
        }

        if (getConfig().MARKERS_SPAWN_ENABLED) {
            getLayerRegistry().register(new SpawnLayer(this));
        }

        if (PlayerTracker.ENABLED) {
            getLayerRegistry().register(new PlayersLayer(this));
        }

        startMarkersTask();
        startBackgroundRender();

        deserializeDirtyRegions();
        deserializeScannedRegions();

        if (!getScannedRegions().isEmpty()) {
            startRender(new FullRender(this, Pl3xMap.api().getConsole()));
        }

        new WorldLoadedEvent(this).callEvent();
        Logger.debug("<green>Loaded <world>"
                .replace("<world>", getName()));
    }

    /**
     * Get the internal name of this world.
     * <p>
     * On Bukkit servers this will be th world name, on other server
     * types this will be the world's dimension identifier.
     *
     * @return world name
     */
    @NotNull
    public String getName() {
        return getKey().toString();
    }

    /**
     * Get the internal server world level.
     *
     * @return world level
     */
    @NotNull
    public ServerLevel getLevel() {
        return this.level;
    }

    /**
     * Get whether this world is enabled.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return getConfig().ENABLED;
    }

    /**
     * Get the world's type.
     *
     * @return world type
     */
    @NotNull
    public Type getType() {
        return this.type;
    }

    /**
     * Get the world's biome seed.
     *
     * @return biome seed
     */
    public long getBiomeSeed() {
        return this.seed;
    }

    /**
     * Get the world's spawn point.
     *
     * @return spawn point
     */
    @NotNull
    public Point getSpawn() {
        return Point.of(getLevel().getSharedSpawnPos());
    }

    /**
     * Get all players on this world.
     *
     * @return all players on this world
     */
    @NotNull
    public abstract Collection<Player> getPlayers();

    /**
     * Get the world's configuration.
     *
     * @return world config
     */
    @NotNull
    public WorldConfig getConfig() {
        return this.config;
    }

    /**
     * Get the world's configured renderer holders.
     *
     * @return renderer holders
     */
    @NotNull
    public Map<Key, RendererHolder> getRendererHolders() {
        return Collections.unmodifiableMap(this.rendererHolders);
    }

    /**
     * Get this world's biome palette registry.
     *
     * @return biome palette registry
     */
    @NotNull
    public BiomePaletteRegistry getBiomePaletteRegistry() {
        return this.biomePaletteRegistry;
    }

    /**
     * Rebuild this world's biome palette registry.
     */
    public void rebuildBiomesPaletteRegistry() {
        this.biomePaletteRegistry = new BiomePaletteRegistry();

        getBiomeRegistry().forEach(biome -> {
            String name = PaletteRegistry.toName("biome", getBiomeRegistry().getKey(biome));
            Palette index = new Palette(getBiomePaletteRegistry().size(), name);
            getBiomePaletteRegistry().register(biome, index);
        });
        getBiomePaletteRegistry().lock();

        try {
            FileUtil.saveGzip(GSON.toJson(getBiomePaletteRegistry().getMap()), getTilesDir().resolve("biomes.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Get this world's biome registry.
     *
     * @return biome registry
     */
    @NotNull
    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    /**
     * Get this world's layer registry.
     *
     * @return layer registry
     */
    @NotNull
    public KeyedRegistry<Layer> getLayerRegistry() {
        return this.layerRegistry;
    }

    /**
     * Check if renderers are paused on this world.
     *
     * @return true if renderers are paused
     */
    public boolean isPaused() {
        return this.paused;
    }

    /**
     * Set is renderers are paused on this world.
     *
     * @param paused true if renderers are paused
     */
    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    /**
     * Load stored scanned region data from disk.
     */
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

    /**
     * Store scanned region data to disk.
     */
    public void serializeScannedRegions() {
        try {
            Files.writeString(this.dataPath.resolve(SCANNED_REGIONS), GSON.toJson(getScannedRegions()));
        } catch (IOException e) {
            Logger.warn(String.format("Failed to serialize render progress for world '%s'", getName()));
            e.printStackTrace();
        }
    }

    /**
     * Load stored dirty regions data from disk.
     */
    private void deserializeDirtyRegions() {
        try {
            final Path file = this.dataPath.resolve(DIRTY_REGIONS);
            if (Files.exists(file)) {
                this.modifiedRegions.addAll(GSON.fromJson(
                        new FileReader(file.toFile()),
                        TypeToken.getParameterized(List.class, RegionCoordinate.class).getType()
                ));
            }
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            Logger.warn(String.format("Failed to deserialize dirty chunks for world '%s'", getName()));
            e.printStackTrace();
        }
    }

    /**
     * Store dirty regions data to disk.
     */
    private void serializeDirtyRegions() {
        try {
            Files.writeString(this.dataPath.resolve(DIRTY_REGIONS), GSON.toJson(this.modifiedRegions));
        } catch (IOException e) {
            Logger.warn(String.format("Failed to serialize dirty chunks for world '%s'", getName()));
            e.printStackTrace();
        }
    }

    /**
     * Add a modified/dirty region.
     *
     * @param region modified/dirty region
     */
    public void addModifiedRegion(@NotNull RegionCoordinate region) {
        if (!this.modifiedRegions.contains(region)) {
            this.modifiedRegions.add(region);
        }
    }

    /**
     * Check if there are modified/dirty regions.
     *
     * @return true if there are modified/dirty regions
     */
    public boolean hasModifiedRegions() {
        return !this.modifiedRegions.isEmpty();
    }

    /**
     * Get the next modified/dirty region in the queue.
     *
     * @return next modified/dirty region in the queue, or null
     */
    @Nullable
    public RegionCoordinate getNextModifiedRegion() {
        return this.modifiedRegions.poll();
    }

    /**
     * Clear scanned region data.
     */
    public void clearScannedRegions() {
        synchronized (this.scannedRegions) {
            this.scannedRegions.clear();
        }
    }

    /**
     * Set scanned region data.
     *
     * @param scannedRegions scanned region data
     */
    public void setScannedRegions(@NotNull LinkedHashMap<RegionCoordinate, Boolean> scannedRegions) {
        synchronized (this.scannedRegions) {
            this.scannedRegions.clear();
            this.scannedRegions.putAll(scannedRegions);
        }
    }

    /**
     * Set a region as scanned
     *
     * @param region region
     */
    public void setScannedRegion(@NotNull RegionCoordinate region) {
        synchronized (this.scannedRegions) {
            if (this.scannedRegions.containsKey(region)) {
                this.scannedRegions.put(region, true);
            }
        }
    }

    /**
     * Get scanned region data.
     *
     * @return scanned region data
     */
    @NotNull
    public LinkedHashMap<RegionCoordinate, Boolean> getScannedRegions() {
        synchronized (this.scannedRegions) {
            return this.scannedRegions;
        }
    }

    /**
     * Check if background render is running.
     *
     * @return true if background render is running
     */
    public boolean hasBackgroundRender() {
        return getBackgroundRender() != null;
    }

    /**
     * Get the current background render.
     *
     * @return current background render, or null
     */
    @Nullable
    public ScheduledFuture<?> getBackgroundRender() {
        return this.backgroundRender;
    }

    /**
     * Start the background render.
     */
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

    /**
     * Stop the background render.
     */
    public void stopBackgroundRender() {
        if (!hasBackgroundRender()) {
            throw new IllegalStateException("Not background rendering");
        }

        this.backgroundRender.cancel(false);
        this.backgroundRender = null;
    }

    /**
     * Start the marker tasks.
     */
    public void startMarkersTask() {
        this.markersUpdater = this.markersExecutor.scheduleAtFixedRate(new UpdateMarkerData(this), ThreadLocalRandom.current().nextInt(1000), 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Stop the marker tasks.
     */
    public void stopMarkersTask() {
        if (this.markersUpdater != null) {
            if (!this.markersUpdater.isCancelled()) {
                this.markersUpdater.cancel(false);
            }
            this.markersUpdater = null;
        }
    }

    /**
     * Check if a render is currently in progress on this world
     *
     * @return true if a render is in progress
     */
    public boolean hasActiveRender() {
        return getActiveRender() != null;
    }

    /**
     * Get the current active render.
     * <p>
     * This does <u>not</u> include the background render.
     *
     * @return current active render
     */
    @Nullable
    public Render getActiveRender() {
        return this.activeRender;
    }

    /**
     * Start a new active render.
     *
     * @param render
     */
    public void startRender(@NotNull Render render) {
        if (hasActiveRender()) {
            throw new IllegalStateException("Already rendering");
        }

        if (hasBackgroundRender()) {
            stopBackgroundRender();
        }

        this.activeRender = render;
        this.backgroundExecutor.submit(render);
    }

    /**
     * Cancel the current active render.
     *
     * @param unloading true if cancel is from unloading the world
     */
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

    /**
     * Finish the current active render.
     */
    public void finishRender() {
        if (!hasActiveRender()) {
            throw new IllegalStateException("No render to finish");
        }

        this.activeRender.finish();
        this.activeRender = null;

        startBackgroundRender();
    }

    /**
     * Unload this world.
     * <p>
     * This is automatically called when the world is unregistered.
     */
    public void unload() {
        if (!isEnabled()) {
            return;
        }

        if (hasActiveRender()) {
            cancelRender(true);
        }

        stopMarkersTask();

        serializeDirtyRegions();
        serializeScannedRegions();
    }

    /**
     * Get the tiles directory for this world
     *
     * @return world tiles directory
     */
    @NotNull
    public Path getTilesDir() {
        return this.tilesPath;
    }

    /**
     * Get the markers directory for this world
     *
     * @return world markers directory
     */
    @NotNull
    public Path getMarkersDir() {
        return getTilesDir().resolve("markers");
    }

    /**
     * Represents a world's type.
     */
    public enum Type {
        OVERWORLD,
        NETHER,
        THE_END,
        CUSTOM;

        private final String name;

        Type() {
            this.name = name().toLowerCase(Locale.ROOT);
        }

        /**
         * Get the world type from a server level.
         *
         * @param level server level
         * @return world type
         */
        @NotNull
        public static Type get(@NotNull ServerLevel level) {
            ResourceKey<Level> key = level.dimension();
            if (key == Level.OVERWORLD) {
                return OVERWORLD;
            } else if (key == Level.NETHER) {
                return NETHER;
            } else if (key == Level.END) {
                return THE_END;
            }
            return CUSTOM;
        }

        @Override
        @NotNull
        public String toString() {
            return this.name;
        }
    }
}
