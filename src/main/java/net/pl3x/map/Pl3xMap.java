package net.pl3x.map;

import net.pl3x.map.command.Pl3xMapCommand;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import net.pl3x.map.httpd.IntegratedServer;
import net.pl3x.map.player.PlayerManager;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.util.Logger;
import net.pl3x.map.util.Pl3xLogger;
import net.pl3x.map.world.WorldManager;
import org.apache.commons.lang.BooleanUtils;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.lang.reflect.Field;

public class Pl3xMap extends JavaPlugin {
    private static Pl3xMap instance;

    private final IntegratedServer integratedServer;
    private final PlayerManager playerManager;
    private final WorldManager worldManager;

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

        integratedServer = new IntegratedServer();
        playerManager = new PlayerManager(this);
        worldManager = new WorldManager();
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

        enable();

        // start bstats metrics
        Metrics metrics = new Metrics(this, 10133);
        metrics.addCustomChart(new SimplePie("internal_web_server", () ->
                BooleanUtils.toStringTrueFalse(Config.HTTPD_ENABLED)));
    }

    @Override
    public void onDisable() {
        disable();
    }

    public void disable() {
        if (Config.HTTPD_ENABLED) {
            integratedServer.stopServer();
        }
    }

    public void enable() {
        // load up configs
        Config.reload(getDataFolder());

        // this has to load after config.yml in order to know if web dir should be overwritten
        // but also before advanced.yml to ensure foliage.png and grass.png are already on disk
        FileUtil.extract("/web/", FileUtil.WEB_DIR.toFile(), !Config.WEB_DIR_READONLY);
        FileUtil.extract("/locale/", FileUtil.LOCALE_DIR.toFile(), false);

        // load language file
        Lang.reload(new File(getDataFolder(), "locale"));

        // start integrated server
        integratedServer.startServer();

        // register command executor
        PluginCommand command = getCommand("map");
        if (command != null) {
            command.setExecutor(new Pl3xMapCommand(this));
        }
    }

    public static Pl3xMap getInstance() {
        return instance;
    }

    public PlayerManager getPlayerManager() {
        return this.playerManager;
    }

    public WorldManager getWorldManager() {
        return this.worldManager;
    }
}
