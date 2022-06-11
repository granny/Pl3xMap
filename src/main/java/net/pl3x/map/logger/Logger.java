package net.pl3x.map.logger;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;

import java.util.logging.Level;

public class Logger {
    public static void debug(String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(String message) {
        info(message, null);
    }

    public static void info(String message, Throwable t) {
        log().log(Level.INFO, message, t);
    }

    public static void warn(String message) {
        warn(message, null);
    }

    public static void warn(String message, Throwable t) {
        log().log(Level.WARNING, message, t);
    }

    public static void severe(String message) {
        severe(message, null);
    }

    public static void severe(String message, Throwable t) {
        log().log(Level.SEVERE, message, t);
    }

    private static java.util.logging.Logger log() {
        return Pl3xMap.getInstance().getLogger();
    }
}
