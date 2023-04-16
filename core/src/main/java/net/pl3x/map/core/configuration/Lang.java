package net.pl3x.map.core.configuration;

import java.nio.file.Path;
import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.util.FileUtil;

@SuppressWarnings("CanBeFinal")
public final class Lang extends AbstractConfig {
    @Key("prefix.command")
    public static String PREFIX_COMMAND = "<white>[<gradient:#C028FF:#5B00FF>Pl3xMap</gradient>]</white> ";

    @Key("httpd.started.success")
    public static String HTTPD_STARTED = "<green>Internal webserver running on <yellow><bind></yellow>:<yellow><port></yellow>";
    @Key("httpd.started.error")
    public static String HTTPD_START_ERROR = "<red>Internal webserver could not start";
    @Key("httpd.stopped.success")
    public static String HTTPD_STOPPED = "<green>Internal webserver stopped";
    @Key("httpd.stopped.error")
    public static String HTTPD_STOP_ERROR = "<red>An error occurred with the internal webserver";
    @Key("httpd.disabled")
    public static String HTTPD_DISABLED = "<green>Internal webserver is disabled";

    @Key("ui.layer.players")
    public static String UI_LAYER_PLAYERS = "Players";
    @Key("ui.layer.spawn")
    public static String UI_LAYER_SPAWN = "Spawn";
    @Key("ui.layer.worldborder")
    public static String UI_LAYER_WORLDBORDER = "World Border";

    private static final Lang CONFIG = new Lang();

    public static void reload() {
        Path localeDir = Pl3xMap.api().getMainDir().resolve("locale");

        // extract locale dir from jar
        FileUtil.extractDir("/locale/", localeDir, false);

        CONFIG.reload(localeDir.resolve(Config.LANGUAGE_FILE), Lang.class);
    }
}
