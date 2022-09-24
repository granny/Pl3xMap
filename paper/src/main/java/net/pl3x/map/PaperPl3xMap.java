package net.pl3x.map;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.pl3x.map.addon.BukkitAddonRegistry;
import net.pl3x.map.command.BukkitCommandManager;
import net.pl3x.map.command.BukkitConsole;
import net.pl3x.map.command.Console;
import net.pl3x.map.configuration.AdvancedConfig;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.configuration.PlayerTracker;
import net.pl3x.map.event.EventRegistry;
import net.pl3x.map.heightmap.HeightmapRegistry;
import net.pl3x.map.httpd.IntegratedServer;
import net.pl3x.map.httpd.UndertowServer;
import net.pl3x.map.image.IconRegistry;
import net.pl3x.map.image.io.IO;
import net.pl3x.map.image.io.Png;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.palette.BlockPaletteRegistry;
import net.pl3x.map.player.BukkitPlayerListener;
import net.pl3x.map.player.BukkitPlayerRegistry;
import net.pl3x.map.render.RendererHolder;
import net.pl3x.map.render.RendererRegistry;
import net.pl3x.map.task.UpdateSettingsData;
import net.pl3x.map.world.BukkitWorldListener;
import net.pl3x.map.world.BukkitWorldRegistry;
import org.apache.commons.lang.BooleanUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.AdvancedPie;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PaperPl3xMap extends JavaPlugin implements Pl3xMap {
    private static PaperPl3xMap INSTANCE;

    private final ScheduledExecutorService settingsDataExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-WorldData").build());
    private ScheduledFuture<?> settingsDataTask;

    private final BukkitConsole console = new BukkitConsole();

    private BukkitPlayerListener playerListener;
    private BukkitWorldListener worldListener;

    private IntegratedServer integratedServer;

    private BukkitAddonRegistry addonRegistry;
    private EventRegistry eventRegistry;
    private HeightmapRegistry heightmapRegistry;
    private IconRegistry iconRegistry;
    private BlockPaletteRegistry blockPaletteRegistry;
    private BukkitPlayerRegistry playerRegistry;
    private RendererRegistry rendererRegistry;
    private BukkitWorldRegistry worldRegistry;

    public static PaperPl3xMap getInstance() {
        return INSTANCE;
    }

    public PaperPl3xMap() {
        super();

        INSTANCE = this;

        try {
            Field api = Pl3xMap.Provider.class.getDeclaredField("api");
            api.setAccessible(true);
            api.set(null, this);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @Override
    @NotNull
    public java.util.logging.Logger getLogger() {
        return Logger.getInstance();
    }

    @Override
    public void onEnable() {
        // test for Paper
        try {
            Class.forName("io.papermc.paper.configuration.PaperConfigurations");
        } catch (ClassNotFoundException e) {
            Logger.severe("This plugin requires Paper or one of its forks to run");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // load up configs
        saveDefaultConfig();
        Config.reload();
        Lang.reload();
        AdvancedConfig.reload();
        PlayerTracker.reload();

        // register built in tile image types
        IO.register("png", new Png());

        // register listeners
        this.playerListener = new BukkitPlayerListener();
        this.worldListener = new BukkitWorldListener(this);

        getServer().getPluginManager().registerEvents(getPlayerListener(), this);
        getServer().getPluginManager().registerEvents(getWorldListener(), this);

        // integrated server
        this.integratedServer = new UndertowServer();

        // setup managers
        this.addonRegistry = new BukkitAddonRegistry();
        this.eventRegistry = new EventRegistry();
        this.heightmapRegistry = new HeightmapRegistry();
        this.iconRegistry = new IconRegistry();
        this.blockPaletteRegistry = new BlockPaletteRegistry();
        this.playerRegistry = new BukkitPlayerRegistry();
        this.rendererRegistry = new RendererRegistry();
        this.worldRegistry = new BukkitWorldRegistry();

        // register command manager
        try {
            new BukkitCommandManager(this);
        } catch (Exception e) {
            Logger.warn("Failed to initialize command manager");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // setup bStats metrics
        Metrics metrics = new Metrics(this, 10133);
        metrics.addCustomChart(new SimplePie("language_used", () ->
                Config.LANGUAGE_FILE.replace("lang-", "").replace(".yml", "")
        ));
        metrics.addCustomChart(new SimplePie("internal_web_server", () ->
                BooleanUtils.toStringTrueFalse(Config.HTTPD_ENABLED)
        ));
        metrics.addCustomChart(new AdvancedPie("installed_addons", () -> {
            Map<String, Integer> map = new HashMap<>();
            getAddonRegistry().entries().forEach((key, addon) -> map.put(addon.getName(), 1));
            return map;
        }));
        metrics.addCustomChart(new AdvancedPie("renderers_used", () -> {
            Map<String, Integer> map = new HashMap<>();
            getWorldRegistry().entries().forEach((key, world) ->
                    world.getConfig().RENDER_RENDERERS.forEach(rendererName -> {
                        RendererHolder renderer = getRendererRegistry().get(rendererName);
                        if (renderer != null) {
                            int count = map.getOrDefault(renderer.getName(), 0);
                            map.put(renderer.getName(), count + 1);
                        }
                    })
            );
            return map;
        }));
        metrics.addCustomChart(new DrilldownPie("plugin_version", () -> {
            String[] version = getVersion().split("-");
            return Map.of(version[0], Map.of(version[1], 1));
        }));

        // enable the plugin
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    @Override
    public void enable() {
        // register built-in heightmaps
        getHeightmapRegistry().register();

        // register built-in renderers
        getRendererRegistry().register();

        // enable addons
        getAddonRegistry().register();

        // start integrated server
        getIntegratedServer().startServer();

        // load up worlds already loaded in bukkit
        Bukkit.getWorlds().forEach(getWorldRegistry()::register);

        // load up players already connected to the server
        Bukkit.getOnlinePlayers().forEach(getPlayerRegistry()::register);

        // register events
        if (this.worldListener != null) {
            this.worldListener.registerEvents();
        }

        // start updating world data
        this.settingsDataTask = this.settingsDataExecutor.scheduleAtFixedRate(new UpdateSettingsData(), 1, 1, TimeUnit.SECONDS);
    }

    @Override
    public void disable() {
        // stop updating world data
        if (this.settingsDataTask != null) {
            if (!this.settingsDataTask.isCancelled()) {
                this.settingsDataTask.cancel(false);
            }
            this.settingsDataTask = null;
        }

        // unregister world events
        if (this.worldListener != null) {
            this.worldListener.unregisterEvents();
        }

        // stop integrated server
        getIntegratedServer().stopServer();

        // unload all map worlds
        getWorldRegistry().unregister();

        // disable addons
        getAddonRegistry().unregister();

        // unregister heightmaps
        getHeightmapRegistry().unregister();

        // unregister renderers
        getRendererRegistry().unregister();

        // unregister icons
        getIconRegistry().entries().forEach((key, image) -> getIconRegistry().unregister(key));

        // unload all players
        getPlayerRegistry().unregister();
    }

    @Override
    @NotNull
    public Impl getImpl() {
        return Impl.PAPER;
    }

    @Override
    @NotNull
    public String getVersion() {
        return getDescription().getVersion();
    }

    @Override
    public int getCurrentTick() {
        return getServer().getCurrentTick();
    }

    @Override
    @NotNull
    public Path getMainDir() {
        return getDataFolder().toPath();
    }

    @Override
    @NotNull
    public Console getConsole() {
        return this.console;
    }

    @Override
    @NotNull
    public BukkitPlayerListener getPlayerListener() {
        return this.playerListener;
    }

    @Override
    @NotNull
    public BukkitWorldListener getWorldListener() {
        return this.worldListener;
    }

    @Override
    @NotNull
    public IntegratedServer getIntegratedServer() {
        return this.integratedServer;
    }

    @Override
    @NotNull
    public BukkitAddonRegistry getAddonRegistry() {
        return this.addonRegistry;
    }

    @Override
    @NotNull
    public EventRegistry getEventRegistry() {
        return this.eventRegistry;
    }

    @Override
    @NotNull
    public HeightmapRegistry getHeightmapRegistry() {
        return this.heightmapRegistry;
    }

    @Override
    @NotNull
    public IconRegistry getIconRegistry() {
        return this.iconRegistry;
    }

    @Override
    @NotNull
    public BlockPaletteRegistry getBlockPaletteRegistry() {
        return this.blockPaletteRegistry;
    }

    @Override
    @NotNull
    public BukkitPlayerRegistry getPlayerRegistry() {
        return this.playerRegistry;
    }

    @Override
    @NotNull
    public RendererRegistry getRendererRegistry() {
        return this.rendererRegistry;
    }

    @Override
    @NotNull
    public BukkitWorldRegistry getWorldRegistry() {
        return this.worldRegistry;
    }
}
