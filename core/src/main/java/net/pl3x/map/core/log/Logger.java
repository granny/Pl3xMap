package net.pl3x.map.core.log;

import net.pl3x.map.core.configuration.Config;
import org.checkerframework.checker.nullness.qual.NonNull;

public class Logger {
    public static void debug(@NonNull String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(@NonNull String msg) {
        System.out.println(msg);
    }

    public static void severe(@NonNull String msg) {
        System.out.println("<red>[ERROR] " + msg);
    }

    public static void warn(@NonNull String msg) {
        System.out.println("<yellow>[WARN] " + msg);
    }
}
