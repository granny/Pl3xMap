package net.pl3x.map.core.log;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.configuration.Config;
import net.pl3x.map.core.configuration.Lang;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class Logger {
    public static void debug(@NonNull String message) {
        if (Config.DEBUG_MODE) {
            log("<gray>[<yellow>DEBUG</yellow>] " + message);
        }
    }

    public static void info(@NonNull String message) {
        log("<gray>[INFO] " + message);
    }

    public static void severe(@NonNull String message) {
        severe(message, null);
    }

    public static void severe(@NonNull String message, @Nullable Throwable throwable) {
        log("<gray>[<red>ERROR</red>]</gray> <red>" + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    public static void warn(@NonNull String message) {
        warn(message, null);
    }

    public static void warn(@NonNull String message, @Nullable Throwable throwable) {
        log("<gray>[<yellow>WARN</yellow>]</gray> <yellow>" + message);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    private static void log(String message) {
        Pl3xMap.api().adventure().console().sendMessage(Lang.parse(Lang.PREFIX_COMMAND + message));
    }
}
