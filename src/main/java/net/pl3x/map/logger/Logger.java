package net.pl3x.map.logger;

import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logger {
    public static void debug(String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(String message) {
        Lang.send(Bukkit.getConsoleSender(), message);
    }

    public static void warn(String message) {
        log().log(Level.WARNING, message);
    }

    public static void severe(String message) {
        log().log(Level.SEVERE, message);
    }

    public static java.util.logging.Logger log() {
        return Pl3xMap.getInstance().getLogger();
    }
}
