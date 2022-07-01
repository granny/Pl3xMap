package net.pl3x.map;

import java.lang.reflect.Field;
import net.pl3x.map.command.CommandManager;
import net.pl3x.map.configuration.Advanced;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.httpd.IntegratedServer;
import net.pl3x.map.logger.LogFilter;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.logger.Pl3xLogger;
import net.pl3x.map.player.PlayerListener;
import net.pl3x.map.render.scanner.Scanner;
import net.pl3x.map.render.scanner.Scanners;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.WorldListener;
import net.pl3x.map.world.WorldManager;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class Pl3xMap extends JavaPlugin {
    private static Pl3xMap instance;

    private WorldListener worldListener;

    public static Pl3xMap getInstance() {
        return instance;
    }

    public Pl3xMap() {
        super();

        instance = this;

        try {
            // try to hack in a fancier logger :3
            Field logger = JavaPlugin.class.getDeclaredField("logger");
            logger.trySetAccessible();
            logger.set(this, new Pl3xLogger());
        } catch (Throwable ignore) {
        }

        // this filter lets us hide undertow/xnio/jboss messages to the logger
        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new LogFilter());
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

        // extract default worlds.yml file for nether and end default stuff
        FileUtil.extract("worlds.yml", false);

        // extract locale from jar
        FileUtil.extract("/locale/", FileUtil.LOCALE_DIR, false);

        // register bukkit listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.worldListener = new WorldListener(this);
        getServer().getPluginManager().registerEvents(this.worldListener, this);

        // load up configs
        Config.reload();
        Lang.reload();

        // this has to load after configs in order to know
        // what web dir is and if it should be overwritten
        // but before advanced config to load biome colors
        FileUtil.extract("/web/", MapWorld.WEB_DIR, !Config.WEB_DIR_READONLY);

        // load remaining configs
        Advanced.reload();

        // start up built in scanners
        //noinspection unused
        Class<? extends Scanner> basic = Scanners.BASIC;

        // enable the plugin
        enable();

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
    }

    @Override
    public void onDisable() {
        disable();
    }

    public void enable() {
        // start integrated server
        IntegratedServer.INSTANCE.startServer();

        // load up worlds already loaded in bukkit
        Bukkit.getWorlds().forEach(WorldManager.INSTANCE::loadWorld);

        // register events
        if (this.worldListener != null) {
            this.worldListener.registerEvents();
        }
    }

    public void disable() {
        // unregister events
        if (this.worldListener != null) {
            this.worldListener.unregisterEvents();
        }

        // stop integrated server
        IntegratedServer.INSTANCE.stopServer();

        // unload all map worlds
        WorldManager.INSTANCE.unload();
    }
}
