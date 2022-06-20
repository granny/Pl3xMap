package net.pl3x.map;

import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.AbstractConfig;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.httpd.IntegratedServer;
import net.pl3x.map.logger.LogFilter;
import net.pl3x.map.logger.Logger;
import net.pl3x.map.logger.Pl3xLogger;
import net.pl3x.map.player.PlayerListener;
import net.pl3x.map.render.task.ThreadManager;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.MapWorld;
import net.pl3x.map.world.WorldListener;
import net.pl3x.map.world.WorldManager;
import org.apache.commons.lang.BooleanUtils;
import org.apache.logging.log4j.LogManager;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;

public class Pl3xMap extends JavaPlugin {
    private static Pl3xMap instance;

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
            Class.forName("com.destroystokyo.paper.PaperConfig");
        } catch (ClassNotFoundException e) {
            Logger.severe("This plugin requires Paper or one of its forks to run");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // extract default worlds.yml file for nether and end default stuff
        FileUtil.extract("worlds.yml", false);

        // extract folders from jar
        FileUtil.extract("/data/", AbstractConfig.DATA_DIR, false);
        FileUtil.extract("/locale/", AbstractConfig.LOCALE_DIR, false);
        FileUtil.extract("/renderers/", AbstractConfig.RENDERER_DIR, false);

        // register bukkit listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WorldListener(), this);

        // load up configs (load twice to fix no comments on first load bug)
        Config.reload();
        Config.reload();
        Lang.reload();
        Lang.reload();

        // this has to load after configs in order to know
        // what web dir is and if it should be overwritten
        FileUtil.extract("/web/", MapWorld.WEB_DIR, !Config.WEB_DIR_READONLY);

        // enable the plugin
        enable();

        // register command executor
        PluginCommand command = getCommand("map");
        if (command != null) {
            command.setExecutor(new Pl3xMapCommand(this));
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
    }

    public void disable() {
        // stop all running threads
        ThreadManager.INSTANCE.shutdown();

        // stop integrated server
        IntegratedServer.INSTANCE.stopServer();

        // unload all map worlds
        WorldManager.INSTANCE.unload();
    }
}
