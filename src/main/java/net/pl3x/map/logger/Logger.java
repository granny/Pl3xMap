package net.pl3x.map.logger;

import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.Pl3xMap;
import net.pl3x.map.configuration.Config;
import net.pl3x.map.configuration.Lang;

public class Logger {
    public static void debug(String message) {
        if (Config.DEBUG_MODE) {
            info("<yellow>[DEBUG]</yellow> " + message);
        }
    }

    public static void info(String message) {
        log().info(Lang.parse(message));
    }

    public static void info(String message, TagResolver.Single... placeholders) {
        log().info(Lang.parse(message, placeholders));
    }

    public static void warn(String message) {
        log().warn(Lang.parse(message));
    }

    public static void warn(String message, TagResolver.Single... placeholders) {
        log().warn(Lang.parse(message, placeholders));
    }

    public static void error(String message) {
        log().error(Lang.parse(message));
    }

    public static void error(String message, TagResolver.Single... placeholders) {
        log().error(Lang.parse(message, placeholders));
    }

    public static ComponentLogger log() {
        return Pl3xMap.getInstance().getComponentLogger();
    }
}
