package net.pl3x.map.core;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.httpd.HttpdServer;
import net.pl3x.map.core.image.io.IO;
import net.pl3x.map.core.player.PlayerRegistry;
import net.pl3x.map.core.registry.BlockRegistry;
import net.pl3x.map.core.registry.IconRegistry;
import net.pl3x.map.core.registry.RendererRegistry;
import net.pl3x.map.core.registry.WorldRegistry;
import net.pl3x.map.core.renderer.heightmap.HeightmapRegistry;
import net.pl3x.map.core.renderer.task.RegionProcessor;
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
    public abstract Path getMainDir();

    public abstract int getColorForPower(byte power);

    public abstract Block getFlower(World world, Biome biome, int blockX, int blockY, int blockZ);

    public void enable() {
        // load up configs
        Config.reload();
        Lang.reload();
        ColorsConfig.reload();

        // load blocks _after_ we loaded colors
        Blocks.registerDefaults();
        loadBlocks();

        // create the executor service
        this.renderExecutor = ThreadFactory.createService("Pl3xMap-Renderer", Config.RENDER_THREADS);

        // register built in tile image types
        IO.register();

        // register built-in heightmaps
        getHeightmapRegistry().register();

        // register built-in renderers
        getRendererRegistry().register();

        // load up already loaded worlds
        loadWorlds();

        // load up players already connected to the server
        loadPlayers();

        // start integrated server
        getHttpdServer().startServer();

        // start tasks
        getRegionProcessor().start(10000L);
    }

    public void disable() {
        // stop tasks
        getRegionProcessor().stop();

        // stop integrated server
        getHttpdServer().stopServer();

        // unload all players
        //getPlayerRegistry().unregister();

        // unload all map worlds
        getWorldRegistry().unregister();

        // unregister renderers
        getRendererRegistry().unregister();

        // unregister icons
        getIconRegistry().unregister();

        // unregister heightmaps
        getHeightmapRegistry().unregister();

        // unregister tile image types
        IO.unregister();
    }

    public abstract void loadBlocks();

    public abstract void loadWorlds();

    public abstract void loadPlayers();

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

        public ThreadFactory(String name, int threads) {
            this.name = name;
            this.threads = threads;
        }

        public static ExecutorService createService(String name) {
            return createService(new ThreadFactory(name, 1));
        }

        public static ExecutorService createService(String name, int threads) {
            int max = Runtime.getRuntime().availableProcessors() / 2;
            int parallelism = Mathf.clamp(1, max, threads < 1 ? max : threads);
            return createService(new ThreadFactory(name, parallelism));
        }

        private static ExecutorService createService(ThreadFactory factory) {
            return new ForkJoinPool(factory.threads, factory, null, false);
        }

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            // use current classloader, this fixes ClassLoading issues with forge
            thread.setContextClassLoader(Pl3xMap.class.getClassLoader());
            thread.setName(this.threads > 1 ? String.format("%s-%d", this.name, this.id.getAndIncrement()) : this.name);
            return thread;
        }
    }
}
