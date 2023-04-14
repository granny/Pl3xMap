package net.pl3x.map.core;

import java.nio.file.Path;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicInteger;
import net.pl3x.map.core.configuration.ColorsConfig;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import net.pl3x.map.core.heightmap.HeightmapRegistry;
import net.pl3x.map.core.httpd.HttpdServer;
import net.pl3x.map.core.image.io.IO;
import net.pl3x.map.core.registry.BlockRegistry;
import net.pl3x.map.core.registry.IconRegistry;
import net.pl3x.map.core.registry.RendererRegistry;
import net.pl3x.map.core.registry.WorldRegistry;
import net.pl3x.map.core.util.Mathf;
import net.pl3x.map.core.util.SpiFix;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class Pl3xMap {
    @NonNull
    public static Pl3xMap api() {
        return Provider.api();
    }

    private HttpdServer httpdServer;

    private BlockRegistry blockRegistry;
    private HeightmapRegistry heightmapRegistry;
    private IconRegistry iconRegistry;
    private RendererRegistry rendererRegistry;
    private WorldRegistry worldRegistry;

    private ExecutorService executor;

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

        // setup registries
        this.blockRegistry = new BlockRegistry();
        this.heightmapRegistry = new HeightmapRegistry();
        this.iconRegistry = new IconRegistry();
        this.rendererRegistry = new RendererRegistry();
        this.worldRegistry = new WorldRegistry();
    }

    @NonNull
    public HttpdServer getHttpdServer() {
        return this.httpdServer;
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
    public RendererRegistry getRendererRegistry() {
        return this.rendererRegistry;
    }

    @NonNull
    public WorldRegistry getWorldRegistry() {
        return this.worldRegistry;
    }

    @Nullable
    public ExecutorService getExecutor() {
        return this.executor;
    }

    @NonNull
    public abstract Path getMainDir();

    public void enable() {
        // load up configs
        Config.reload();
        Lang.reload();
        ColorsConfig.reload();

        // create the executor service
        this.executor = ThreadFactory.createService(Config.RENDER_THREADS);

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
    }

    public void disable() {
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

    public abstract void loadWorlds();

    public abstract void loadPlayers();

    public static final class Provider {
        static Pl3xMap api;

        @NonNull
        public static Pl3xMap api() {
            return Provider.api;
        }
    }

    private static final class ThreadFactory implements ForkJoinPool.ForkJoinWorkerThreadFactory {
        private final AtomicInteger id = new AtomicInteger();

        private static ExecutorService createService(int threads) {
            int max = Runtime.getRuntime().availableProcessors() / 2;
            int parallelism = Mathf.clamp(1, max, threads < 1 ? max : threads);
            return new ForkJoinPool(parallelism, new ThreadFactory(), null, false);
        }

        @Override
        public ForkJoinWorkerThread newThread(ForkJoinPool pool) {
            ForkJoinWorkerThread thread = ForkJoinPool.defaultForkJoinWorkerThreadFactory.newThread(pool);
            // use current classloader, this fixes ClassLoading issues with forge
            thread.setContextClassLoader(Pl3xMap.class.getClassLoader());
            thread.setName("Pl3xMap-" + this.id.getAndIncrement());
            return thread;
        }
    }
}
