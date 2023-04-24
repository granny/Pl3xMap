package net.pl3x.map.core.log;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.pl3x.map.core.configuration.Config;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.LoggerFactory;

public class Logger {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger("Pl3xMap");

    public static void debug(@NonNull String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(@NonNull String message) {
        log.info(strip(message));
    }

    public static void severe(@NonNull String message) {
        log.error(strip("<red>[ERROR] " + message));
    }

    public static void warn(@NonNull String message) {
        log.warn(strip("<yellow>[WARN] " + message));
    }

    // todo - temporarily strip colors until we can figure this out
    private static @NonNull String strip(@NonNull String message) {
        return MiniMessage.miniMessage().stripTags(message);
    }
}
