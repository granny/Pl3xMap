package net.pl3x.map.core.log;

import net.pl3x.map.core.configuration.Config;

public class Logger {
    public static void debug(String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(String msg) {
        System.out.println(msg);
    }

    public static void severe(String msg) {
        System.out.println("<red>[ERROR] " + msg);
    }

    public static void warn(String msg) {
        System.out.println("<yellow>[WARN] " + msg);
    }
}
