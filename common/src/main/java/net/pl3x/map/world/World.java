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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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
import net.pl3x.map.configuration.WorldConfig;
import net.pl3x.map.coordinate.ChunkCoordinate;
import net.pl3x.map.coordinate.RegionCoordinate;
import net.pl3x.map.event.world.WorldLoadedEvent;
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
import net.pl3x.map.render.job.BackgroundRender;
import net.pl3x.map.render.job.FullRender;
import net.pl3x.map.render.job.Render;
import net.pl3x.map.task.UpdateMarkerData;
import net.pl3x.map.util.FileUtil;
import org.jetbrains.annotations.NotNull;

public abstract class World extends Keyed {
    public static final Path WEB_DIR = Config.WEB_DIR.startsWith("/") ? Path.of(Config.WEB_DIR) : FileUtil.MAIN_DIR.resolve(Config.WEB_DIR);
    public static final Path TILES_DIR = WEB_DIR.resolve("tiles");

    private static final String DIRTY_CHUNKS = "dirty_chunks.json";
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

    private final BiomePaletteRegistry biomePaletteRegistry;
    private final Registry<Biome> biomeRegistry;
    private final KeyedRegistry<Layer> layerRegistry;

    private final Path dataPath;
    private final Path tilesPath;

    private final ScheduledExecutorService backgroundExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Background").build());
    private final ScheduledExecutorService markersExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-Markers").build());

    private ScheduledFuture<?> backgroundRender;
    private ScheduledFuture<?> markersUpdater;

    private Render activeRender = null;

    private final Set<RegionCoordinate> modifiedRegions = ConcurrentHashMap.newKeySet();
    private final LinkedHashMap<RegionCoordinate, Boolean> scannedRegions = new LinkedHashMap<>();

    private boolean alreadyInitialized;
    private boolean paused;

    public World(Key key, ServerLevel level) {
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

    public void init() {
        if (this.alreadyInitialized) {
            throw new IllegalStateException("World already initialized!");
        }
        this.alreadyInitialized = true;

        getConfig().reload();

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

        if (getConfig().MARKERS_WORLDBORDER_ENABLED) {
            getLayerRegistry().register(new WorldBorderLayer(this));
        }

        if (getConfig().MARKERS_SPAWN_ENABLED) {
            getLayerRegistry().register(new SpawnLayer(this));
        }

        if (getConfig().MARKERS_PLAYERS_ENABLED) {
            getLayerRegistry().register(new PlayersLayer(this));
        }

        startMarkersTask();
        startBackgroundRender();

        deserializeDirtyChunks();
        deserializeScannedRegions();

        if (!getScannedRegions().isEmpty()) {
            startRender(new FullRender(this, Pl3xMap.api().getConsole()));
        }

        new WorldLoadedEvent(this).callEvent();
        Logger.debug("<green>Loaded <world>"
                .replace("<world>", getName()));
    }

    @NotNull
    public String getName() {
        return getKey().toString();
    }

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

    @NotNull
    public Type getType() {
        return this.type;
    }

    public long getBiomeSeed() {
        return this.seed;
    }

    @NotNull
    public Point getSpawn() {
        return Point.of(getLevel().getSharedSpawnPos());
    }

    @NotNull
    public abstract Collection<Player> getPlayers();

    @NotNull
    public WorldConfig getConfig() {
        return this.config;
    }

    public BiomePaletteRegistry getBiomePaletteRegistry() {
        return this.biomePaletteRegistry;
    }

    public Registry<Biome> getBiomeRegistry() {
        return this.biomeRegistry;
    }

    public KeyedRegistry<Layer> getLayerRegistry() {
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
                this.modifiedRegions.addAll(GSON.fromJson(
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
            Files.writeString(this.dataPath.resolve(DIRTY_CHUNKS), GSON.toJson(this.modifiedRegions));
        } catch (IOException e) {
            Logger.warn(String.format("Failed to serialize dirty chunks for world '%s'", getName()));
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

        if (hasBackgroundRender()) {
            stopBackgroundRender();
        }

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

    public enum Type {
        OVERWORLD,
        NETHER,
        THE_END,
        CUSTOM;

        private final String name;

        Type() {
            this.name = name().toLowerCase(Locale.ROOT);
        }

        public static Type get(ServerLevel level) {
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
        public String toString() {
            return this.name;
        }
    }
}
