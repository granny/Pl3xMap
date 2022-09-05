package net.pl3x.map.logger;

import net.pl3x.map.Pl3xMapPlugin;
import net.pl3x.map.configuration.Config;

public class Logger {
    public static void debug(String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(String message) {
        log().info(message);
    }

    public static void warn(String message) {
        log().warning(message);
    }

    public static void severe(String message) {
        log().severe(message);
    }

    public static java.util.logging.Logger log() {
        return Pl3xMapPlugin.getInstance().getLogger();
    }
}
