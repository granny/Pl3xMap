package net.pl3x.map.configuration;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.io.File;

public class Lang extends AbstractConfig {
    @Key("command.prefix")
    public static String COMMAND_PREFIX = "<white>[<gradient:#C028FF:#5B00FF>Pl3xMap</gradient>]</white> ";
    @Key("logger.prefix")
    public static String LOGGER_PREFIX = "<dark_aqua>[<light_purple>Pl3xMap</light_purple>]</dark_aqua> ";

    public static String COMMAND_USAGE;
    public static String ERROR_UNKNOWN_ERROR;
    public static String ERROR_UNKNOWN_SUBCOMMAND;

    public static String COMMAND_HELP_SUBCOMMANDS_TITLE;
    public static String COMMAND_HELP_SUBCOMMANDS_FULL_COMMAND;
    public static String COMMAND_HELP_SUBCOMMANDS_ENTRY;
    public static String COMMAND_HELP_SUBCOMMANDS_ENTRY_PREFIX_LAST;
    public static String COMMAND_HELP_SUBCOMMANDS_ENTRY_PREFIX;

    public static String ERROR_MUST_SPECIFY_PLAYER;
    public static String ERROR_NO_SUCH_PLAYER;
    public static String ERROR_MUST_SPECIFY_WORLD;
    public static String ERROR_NO_SUCH_WORLD;
    public static String ERROR_WORLD_DISABLED;

    public static String CMD_HELP_DESCRIPTION;

    public static String CMD_HIDE_DESCRIPTION;
    public static String CMD_HIDE_ALREADY_HIDDEN;
    public static String CMD_HIDE_SUCCESS;

    public static String CMD_RELOAD_DESCRIPTION;
    public static String CMD_RELOAD_SUCCESS;

    public static String CMD_SHOW_DESCRIPTION;
    public static String CMD_SHOW_NOT_HIDDEN;
    public static String CMD_SHOW_SUCCESS;

    @Key("httpd.started.success")
    public static String HTTPD_STARTED = "<green>Internal webserver running on <yellow><bind></yellow>:<yellow><port></yellow>";
    @Key("httpd.stopped.success")
    public static String HTTPD_STOPPED = "<green>Internal webserver stopped";
    @Key("httpd.started.error")
    public static String HTTPD_START_ERROR = "<red>Internal webserver could not start";
    @Key("httpd.stopped.error")
    public static String HTTPD_STOP_ERROR = "<red>An error occurred with the internal webserver";
    @Key("httpd.disabled")
    public static String HTTPD_DISABLED = "<green>Internal webserver is disabled in config.yml";

    private static final Lang CONFIG = new Lang();

    public static void reload(File dir) {
        CONFIG.reload(dir, Config.LANGUAGE_FILE, Lang.class);
    }

    public static void send(Audience recipient, String msg, TagResolver.Single... placeholders) {
        send(recipient, true, msg, placeholders);
    }

    public static void send(Audience recipient, boolean prefix, String msg, TagResolver.Single... placeholders) {
        for (String part : msg.split("\\n")) {
            recipient.sendMessage(parse((prefix ? Lang.COMMAND_PREFIX : "") + part, placeholders));
        }
    }

    public static Component parse(String msg, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(msg, placeholders);
    }
}
