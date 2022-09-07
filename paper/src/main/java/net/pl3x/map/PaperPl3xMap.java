package net.pl3x.map;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import net.kyori.adventure.audience.Audience;
import net.pl3x.map.addon.PaperAddonRegistry;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.configuration.AdvancedConfig;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.event.EventRegistry;
import net.pl3x.map.heightmap.HeightmapRegistry;
import net.pl3x.map.httpd.IntegratedServer;
import net.pl3x.map.httpd.UndertowServer;
import net.pl3x.map.image.IconRegistry;
import net.pl3x.map.image.io.IO;
import net.pl3x.map.image.io.Png;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.palette.PaletteRegistry;
import net.pl3x.map.player.BukkitPlayerRegistry;
import net.pl3x.map.player.PlayerListener;
import net.pl3x.map.render.RendererRegistry;
import net.pl3x.map.task.UpdatePlayerData;
import net.pl3x.map.task.UpdateWorldData;
import net.pl3x.map.world.BukkitWorldListener;
import net.pl3x.map.world.BukkitWorldRegistry;
import org.apache.commons.lang.BooleanUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public class PaperPl3xMap extends JavaPlugin implements Pl3xMap {
    private static PaperPl3xMap INSTANCE;

    private final ScheduledExecutorService playerDataExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-PlayerData").build());
    private final ScheduledExecutorService worldDataExecutor = Executors.newScheduledThreadPool(1, new ThreadFactoryBuilder().setNameFormat("Pl3xMap-WorldData").build());

    private ScheduledFuture<?> playerDataTask;
    private ScheduledFuture<?> worldDataTask;

    private BukkitWorldListener worldListener;

    private PaperAddonRegistry addonRegistry;
    private EventRegistry eventRegistry;
    private HeightmapRegistry heightmapRegistry;
    private IconRegistry iconRegistry;
    private IntegratedServer integratedServer;
    private PaletteRegistry paletteRegistry;
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
            return;
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

        // register built in tile image types
        IO.register("png", new Png());

        // register bukkit listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.worldListener = new BukkitWorldListener(this);
        getServer().getPluginManager().registerEvents(this.worldListener, this);

        // setup managers
        this.addonRegistry = new PaperAddonRegistry();
        this.eventRegistry = new EventRegistry();
        this.heightmapRegistry = new HeightmapRegistry();
        this.iconRegistry = new IconRegistry();
        this.integratedServer = new UndertowServer();
        this.paletteRegistry = new PaletteRegistry();
        this.playerRegistry = new BukkitPlayerRegistry();
        this.rendererRegistry = new RendererRegistry();
        this.worldRegistry = new BukkitWorldRegistry();

        // register command manager
        try {
            new CommandManager(this);
        } catch (Exception e) {
            Logger.warn("Failed to initialize command manager");
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // start bstats metrics
        Metrics metrics = new Metrics(this, 10133);
        metrics.addCustomChart(new SimplePie("internal_web_server", () ->
                BooleanUtils.toStringTrueFalse(Config.HTTPD_ENABLED)));

        // enable the plugin
        enable();
    }

    @Override
    public void onDisable() {
        disable();
    }

    public void enable() {
        // register built-in heightmaps
        getHeightmapRegistry().init();

        // register built-in renderers
        getRendererRegistry().init();

        // enable addons
        getAddonRegistry().enableAddons();

        // start integrated server
        getIntegratedServer().startServer();

        // load up worlds already loaded in bukkit
        Bukkit.getWorlds().forEach(getWorldRegistry()::register);

        // register events
        if (this.worldListener != null) {
            this.worldListener.registerEvents();
        }

        // start updating player data
        this.playerDataTask = this.playerDataExecutor.scheduleAtFixedRate(new UpdatePlayerData(), 1, 1, TimeUnit.SECONDS);

        // start updating world data
        this.worldDataTask = this.worldDataExecutor.scheduleAtFixedRate(new UpdateWorldData(), 1, 5, TimeUnit.SECONDS);
    }

    public void disable() {
        // stop updating player data
        if (this.playerDataTask != null) {
            if (!this.playerDataTask.isCancelled()) {
                this.playerDataTask.cancel(false);
            }
            this.playerDataTask = null;
        }

        // stop updating world data
        if (this.worldDataTask != null) {
            if (!this.worldDataTask.isCancelled()) {
                this.worldDataTask.cancel(false);
            }
            this.worldDataTask = null;
        }

        // unregister world events
        if (this.worldListener != null) {
            this.worldListener.unregisterEvents();
        }

        // unload all map worlds
        getWorldRegistry().unregister();

        // stop integrated server
        getIntegratedServer().stopServer();

        // disable addons
        getAddonRegistry().disableAddons();

        // unregister heightmaps
        getHeightmapRegistry().unregisterAll();

        // unregister renderers
        getRendererRegistry().unregisterAll();

        // unregister icons
        getIconRegistry().entries().forEach((key, image) -> getIconRegistry().unregister(key));

        // unload all players
        getPlayerRegistry().unloadAll();
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
    public Audience getConsole() {
        return Bukkit.getConsoleSender();
    }

    @Override
    public PaperAddonRegistry getAddonRegistry() {
        return this.addonRegistry;
    }

    @Override
    public EventRegistry getEventRegistry() {
        return this.eventRegistry;
    }

    @Override
    public HeightmapRegistry getHeightmapRegistry() {
        return this.heightmapRegistry;
    }

    @Override
    public IconRegistry getIconRegistry() {
        return this.iconRegistry;
    }

    @Override
    public IntegratedServer getIntegratedServer() {
        return this.integratedServer;
    }

    @Override
    public PaletteRegistry getPaletteRegistry() {
        return this.paletteRegistry;
    }

    @Override
    public BukkitPlayerRegistry getPlayerRegistry() {
        return this.playerRegistry;
    }

    @Override
    public RendererRegistry getRendererRegistry() {
        return this.rendererRegistry;
    }

    @Override
    public BukkitWorldRegistry getWorldRegistry() {
        return this.worldRegistry;
    }
}
