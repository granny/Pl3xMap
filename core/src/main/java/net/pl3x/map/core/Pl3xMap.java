package net.pl3x.map.core;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.httpd.HttpdServer;
import net.pl3x.map.core.image.io.IO;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.player.PlayerRegistry;
import net.pl3x.map.core.registry.BlockRegistry;
import net.pl3x.map.core.registry.IconRegistry;
import net.pl3x.map.core.registry.RendererRegistry;
import net.pl3x.map.core.registry.WorldRegistry;
import net.pl3x.map.core.renderer.heightmap.HeightmapRegistry;
import net.pl3x.map.core.renderer.task.RegionProcessor;
import net.pl3x.map.core.renderer.task.UpdateSettingsData;
import net.pl3x.map.core.scheduler.Scheduler;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.util.SpiFix;
import net.pl3x.map.core.world.Biome;
import net.pl3x.map.core.world.Block;
import net.pl3x.map.core.world.Blocks;
import net.pl3x.map.core.world.World;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Pl3xMap {
    @NonNull
    public static Pl3xMap api() {
        return Provider.api();
    }

    private HttpdServer httpdServer;
    private RegionProcessor regionProcessor;

    private BlockRegistry blockRegistry;
    private HeightmapRegistry heightmapRegistry;
    private IconRegistry iconRegistry;
    protected PlayerRegistry playerRegistry;
    private RendererRegistry rendererRegistry;
    private WorldRegistry worldRegistry;

    private ExecutorService renderExecutor;
    private Scheduler scheduler;

    public Pl3xMap() {
        // Due to these bugs(?) in spi
        // * relocated libraries cant find their services (xnio fails)
        // * imageio fails to find twelvemonkeys spis at all
        // I am forced to load them all myself instead of relying on the META-INF
        SpiFix.forceRegisterSpis();
    }

    protected void init() {
        // setup internal server
        this.httpdServer = new HttpdServer();

        // setup tasks
        this.regionProcessor = new RegionProcessor();

        // setup registries
        this.blockRegistry = new BlockRegistry();
        this.heightmapRegistry = new HeightmapRegistry();
        this.iconRegistry = new IconRegistry();
        this.playerRegistry = new PlayerRegistry();
        this.rendererRegistry = new RendererRegistry();
        this.worldRegistry = new WorldRegistry();
    }

    @NonNull
    public HttpdServer getHttpdServer() {
        return this.httpdServer;
    }

    @NonNull
    public RegionProcessor getRegionProcessor() {
        return this.regionProcessor;
    }

    @NonNull
    public BlockRegistry getBlockRegistry() {
        return this.blockRegistry;
    }

    @NonNull
    public HeightmapRegistry getHeightmapRegistry() {
        return this.heightmapRegistry;
    }

    @NonNull
    public IconRegistry getIconRegistry() {
        return this.iconRegistry;
    }

    @NonNull
    public PlayerRegistry getPlayerRegistry() {
        return this.playerRegistry;
    }

    @NonNull
    public RendererRegistry getRendererRegistry() {
        return this.rendererRegistry;
    }

    @NonNull
    public WorldRegistry getWorldRegistry() {
        return this.worldRegistry;
    }

    @Nullable
    public ExecutorService getRenderExecutor() {
        return this.renderExecutor;
    }

    @NonNull
    public Scheduler getScheduler() {
        return this.scheduler;
    }

    public abstract void useJar(@NonNull Consumer<Path> consumer);

    @NonNull
    public abstract Path getMainDir();

    public abstract int getColorForPower(byte power);

    @Nullable
    public abstract Block getFlower(@NonNull World world, @NonNull Biome biome, int blockX, int blockY, int blockZ);

    public void enable() {
        // load up configs
        Logger.debug("Loading configs");
        Config.reload();
        Lang.reload();
        ColorsConfig.reload();

        // load blocks _after_ we loaded colors
        Logger.debug("Registering blocks");
        Blocks.registerDefaults();
        loadBlocks();

        // create the executor service
        Logger.debug("Creating services");
        this.renderExecutor = ThreadFactory.createService("Pl3xMap-Renderer", Config.RENDER_THREADS);
        this.scheduler = new Scheduler();

        // register built in tile image types
        Logger.debug("Registering tile image types");
        IO.register();

        // register built-in heightmaps
        Logger.debug("Registering heightmaps");
        getHeightmapRegistry().register();

        // register built-in renderers
        Logger.debug("Registering renderers");
        getRendererRegistry().register();

        // load up already loaded worlds
        Logger.debug("Registering worlds");
        loadWorlds();

        // load up players already connected to the server
        Logger.debug("Registering players");
        loadPlayers();

        // start integrated server
        Logger.debug("Starting internal server");
        getHttpdServer().startServer();

        // start tasks
        Logger.debug("Starting region processor");
        getRegionProcessor().start(10000L);

        Logger.debug("Starting update settings data task");
        getScheduler().addTask(new UpdateSettingsData());
    }

    public void disable() {
        // stop tasks
        Logger.debug("Stopping tasks");
        getScheduler().cancelAll();
        getRegionProcessor().stop();

        // stop integrated server
        Logger.debug("Stopping internal server");
        getHttpdServer().stopServer();

        // unload all players
        Logger.debug("Unregistering players");
        getPlayerRegistry().unregister();

        // unload all map worlds
        Logger.debug("Unregistering worlds");
        getWorldRegistry().unregister();

        // unregister renderers
        Logger.debug("Unregistering renderers");
        getRendererRegistry().unregister();

        // unregister icons
        Logger.debug("Unregistering icons");
        getIconRegistry().unregister();

        // unregister heightmaps
        Logger.debug("Unregistering heightmaps");
        getHeightmapRegistry().unregister();

        // unregister tile image types
        Logger.debug("Unregistering tile image types");
        IO.unregister();
    }

    public abstract void loadBlocks();

    public abstract void loadWorlds();

    public abstract void loadPlayers();

    public abstract int getMaxPlayers();

    public static final class Provider {
        static Pl3xMap api;

        @NonNull
        public static Pl3xMap api() {
            return Provider.api;
        }
    }

    public static final class ThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private final String name;
        private final int threads;

        private final AtomicInteger id = new AtomicInteger();

        public ThreadFactory(@NonNull String name, int threads) {
            this.name = name;
            this.threads = threads;
        }

        @NonNull
        public static ExecutorService createService(@NonNull String name) {
            return createService(new ThreadFactory(name, 1));
        }

        @NonNull
        public static ExecutorService createService(@NonNull String name, int threads) {
            int max = Runtime.getRuntime().availableProcessors() / 2;
            int parallelism = Mathf.clamp(1, max, threads < 1 ? max : threads);
            return createService(new ThreadFactory(name, parallelism));
        }

        @NonNull
        private static ExecutorService createService(@NonNull ThreadFactory factory) {
            return new ForkJoinPool(factory.threads, factory, null, false);
        }

        @Override
        @NonNull
        public ForkJoinWorkerThread newThread(@NonNull ForkJoinPool pool) {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            // use current classloader, this fixes ClassLoading issues with forge
            thread.setContextClassLoader(Pl3xMap.class.getClassLoader());
            thread.setName(this.threads > 1 ? String.format("%s-%d", this.name, this.id.getAndIncrement()) : this.name);
            return thread;
        }
    }
}
