/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package net.pl3x.map.core.world;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.PlayerTracker;
import net.pl3x.map.core.configuration.WorldConfig;
import net.pl3x.map.core.event.world.WorldLoadedEvent;
import net.pl3x.map.core.image.IconImage;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.layer.Layer;
import net.pl3x.map.core.markers.layer.PlayersLayer;
import net.pl3x.map.core.markers.layer.SpawnLayer;
import net.pl3x.map.core.markers.layer.WorldBorderLayer;
import net.pl3x.map.core.player.Player;
import net.pl3x.map.core.registry.BiomeRegistry;
import net.pl3x.map.core.registry.Registry;
import net.pl3x.map.core.renderer.Renderer;
import net.pl3x.map.core.renderer.task.RegionFileWatcher;
import net.pl3x.map.core.renderer.task.UpdateMarkerData;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.util.Mathf;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class World {
    public static final PathMatcher MCA_MATCHER = FileSystems.getDefault().getPathMatcher("glob:**/r.*.*.mca");

    private final String name;
    private final Path markersDirectory;
    private final Path regionDirectory;
    private final Path tilesDirectory;
    private final WorldConfig worldConfig;

    private final long seed;
    private final Point spawn;
    private final Type type;

    private final BiomeManager biomeManager;
    private final BiomeRegistry biomeRegistry;
    private final Registry<@NonNull Layer> layerRegistry;

    private final LoadingCache<@NonNull Long, @NonNull Region> regionCache;
    private final RegionModifiedState regionModifiedState;
    private final RegionFileWatcher regionFileWatcher;
    private final UpdateMarkerData markerTask;
    private final Map<@NonNull String, Renderer.@NonNull Builder> renderers = new LinkedHashMap<>();

    private boolean paused = false;

    public World(@NonNull String name, long seed, @NonNull Point spawn, @NonNull Type type, @NonNull Path regionDirectory, @NonNull WorldConfig worldConfig) {
        this.name = name;
        this.seed = seed;
        this.spawn = spawn;
        this.type = type;

        this.regionDirectory = regionDirectory;
        this.tilesDirectory = FileUtil.getTilesDir().resolve(name.replace(":", "-"));
        this.markersDirectory = getTilesDirectory().resolve("markers");

        if (!Files.exists(this.regionDirectory)) {
            try {
                Files.createDirectories(this.regionDirectory);
            } catch (IOException ignore) {
            }
        }

        this.worldConfig = worldConfig;

        this.biomeManager = new BiomeManager(hashSeed(getSeed()));
        this.biomeRegistry = new BiomeRegistry();
        this.layerRegistry = new Registry<>();

        this.regionCache = Caffeine.newBuilder()
                .expireAfterWrite(1, TimeUnit.MINUTES)
                .maximumSize(100)
                .build(this::loadRegion);

        this.regionModifiedState = new RegionModifiedState(this);
        this.regionFileWatcher = new RegionFileWatcher(this);
        this.markerTask = new UpdateMarkerData(this);

        Pl3xMap.api().getEventRegistry().callEvent(new WorldLoadedEvent(this));

        if (!isEnabled()) {
            return;
        }

        this.regionFileWatcher.start();

        worldConfig.RENDER_RENDERERS.forEach((id, icon) -> {
            Renderer.Builder renderer = Pl3xMap.api().getRendererRegistry().get(id);
            if (renderer == null) {
                return;
            }
            Path path = FileUtil.getWebDir().resolve("images/icon/" + icon + ".png");
            try {
                IconImage image = new IconImage(icon, ImageIO.read(path.toFile()), "png");
                Pl3xMap.api().getIconRegistry().register(image);
            } catch (IOException e) {
                Logger.severe("Cannot load world renderer icon " + path);
                e.printStackTrace();
            }
            this.renderers.put(renderer.key(), renderer);
        });

        if (getConfig().MARKERS_WORLDBORDER_ENABLED) {
            Logger.debug("Registering world border layer");
            getLayerRegistry().register(WorldBorderLayer.KEY, new WorldBorderLayer(this));
        }

        if (getConfig().MARKERS_SPAWN_ENABLED) {
            Logger.debug("Registering spawn layer");
            getLayerRegistry().register(SpawnLayer.KEY, new SpawnLayer(this));
        }

        if (PlayerTracker.ENABLED) {
            Logger.debug("Registering player tracker layer");
            getLayerRegistry().register(PlayersLayer.KEY, new PlayersLayer(this));
        }

        Logger.debug("Checking all region files");
        Pl3xMap.api().getRegionProcessor().addRegions(this, listRegions());

        Logger.debug("Starting marker task");
        Pl3xMap.api().getScheduler().addTask(20, true, this.markerTask);
    }

    public void cleanup() {
        this.regionCache.invalidateAll();
    }

    public @NonNull Path getMarkersDirectory() {
        return this.markersDirectory;
    }

    public @NonNull Path getRegionDirectory() {
        return this.regionDirectory;
    }

    public @NonNull Path getTilesDirectory() {
        return this.tilesDirectory;
    }

    public @NonNull WorldConfig getConfig() {
        return this.worldConfig;
    }

    public @NonNull RegionModifiedState getRegionModifiedState() {
        return this.regionModifiedState;
    }

    public @NonNull RegionFileWatcher getRegionFileWatcher() {
        return this.regionFileWatcher;
    }

    public @NonNull UpdateMarkerData getMarkerTask() {
        return this.markerTask;
    }

    public @NonNull Map<@NonNull String, Renderer.@NonNull Builder> getRenderers() {
        return Collections.unmodifiableMap(this.renderers);
    }

    /**
     * Get whether this world is enabled.
     *
     * @return true if enabled
     */
    public boolean isEnabled() {
        return getConfig().ENABLED;
    }

    public @NonNull String getName() {
        return this.name;
    }

    public long getSeed() {
        return this.seed;
    }

    public @NonNull Point getSpawn() {
        return this.spawn;
    }

    public int getSkylight() {
        return getConfig().RENDER_SKYLIGHT;
    }

    /**
     * Get the world's type.
     *
     * @return world type
     */
    public @NonNull Type getType() {
        return this.type;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public @NonNull BiomeManager getBiomeManager() {
        return this.biomeManager;
    }

    public @NonNull BiomeRegistry getBiomeRegistry() {
        return this.biomeRegistry;
    }

    public @NonNull Registry<Layer> getLayerRegistry() {
        return this.layerRegistry;
    }

    public abstract <@NonNull T> @NonNull T getLevel();

    public abstract long hashSeed(long seed);

    public abstract boolean hasCeiling();

    public abstract int getMinBuildHeight();

    public abstract @NonNull Border getWorldBorder();

    public abstract @NonNull Collection<@NonNull Player> getPlayers();

    public @NonNull Chunk getChunk(@Nullable Region region, int chunkX, int chunkZ) {
        return getRegion(region, chunkX >> 5, chunkZ >> 5).getChunk(chunkX, chunkZ);
    }

    public @NonNull Region getRegion(@Nullable Region region, int regionX, int regionZ) {
        if (region != null && region.getX() == regionX && region.getZ() == regionZ) {
            return region;
        }
        return getRegion(Mathf.asLong(regionX, regionZ));
    }

    private @NonNull Region getRegion(long pos) {
        return this.regionCache.get(pos);
    }

    public void unloadRegion(int regionX, int regionZ) {
        unloadRegion(Mathf.asLong(regionX, regionZ));
    }

    private void unloadRegion(long pos) {
        this.regionCache.invalidate(pos);
    }

    public @NonNull Collection<@NonNull Path> getRegionFiles() {
        try (Stream<Path> stream = Files.list(getRegionDirectory())) {
            return stream.filter(MCA_MATCHER::matches).toList();
        } catch (IOException e) {
            throw new RuntimeException("Failed to list region files in directory '" + getRegionDirectory().toAbsolutePath() + "'", e);
        }
    }

    public @NonNull Collection<@NonNull Point> listRegions() {
        return FileUtil.regionPathsToPoints(this, getRegionFiles());
    }

    private @NonNull Region loadRegion(long pos) {
        int x = Mathf.longToX(pos);
        int z = Mathf.longToZ(pos);
        return new Region(this, x, z, getMCAFile(x, z));
    }

    private @NonNull Path getMCAFile(int regionX, int regionZ) {
        return getRegionDirectory().resolve("r." + regionX + "." + regionZ + ".mca");
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (this.getClass() != o.getClass()) {
            return false;
        }
        World other = (World) o;
        return getLevel() == other.getLevel();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName());
    }

    @Override
    public abstract @NonNull String toString();

    public record Border(double centerX, double centerZ, double size) {
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
         * @param dimension dimension name
         * @return world type
         */
        public static @NonNull Type get(@NonNull String dimension) {
            return switch (dimension) {
                case "minecraft:overworld" -> OVERWORLD;
                case "minecraft:the_nether" -> NETHER;
                case "minecraft:the_end" -> THE_END;
                default -> CUSTOM;
            };
        }

        @Override
        public @NonNull String toString() {
            return this.name;
        }
    }
}
