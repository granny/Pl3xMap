package net.pl3x.map.configuration;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

public class Lang extends AbstractConfig {
    @Key("command.base.prefix")
    public static String COMMAND_BASE_PREFIX;
    @Key("command.base.usage")
    public static String COMMAND_BASE_USAGE;
    @Key("command.base.subcommands.title")
    public static String COMMAND_BASE_SUBCOMMANDS_TITLE;
    @Key("command.base.subcommands.full-command")
    public static String COMMAND_BASE_SUBCOMMANDS_FULL_COMMAND;
    @Key("command.base.subcommands.entry.text")
    public static String COMMAND_BASE_SUBCOMMANDS_ENTRY;
    @Key("command.base.subcommands.entry.prefix")
    public static String COMMAND_BASE_SUBCOMMANDS_ENTRY_PREFIX;
    @Key("command.base.subcommands.entry.prefix-last")
    public static String COMMAND_BASE_SUBCOMMANDS_ENTRY_PREFIX_LAST;

    @Key("command.fullrender.description")
    public static String COMMAND_FULLRENDER_DESCRIPTION;
    @Key("command.fullrender.already-rendering")
    public static String COMMAND_FULLRENDER_ALREADY_RENDERING;
    @Key("command.fullrender.starting")
    public static String COMMAND_FULLRENDER_STARTING;
    @Key("command.fullrender.obtaining-regions")
    public static String COMMAND_FULLRENDER_OBTAINING_REGIONS;
    @Key("command.fullrender.sorting-regions")
    public static String COMMAND_FULLRENDER_SORTING_REGIONS;
    @Key("command.fullrender.found-total-regions")
    public static String COMMAND_FULLRENDER_FOUND_TOTAL_REGIONS;
    @Key("command.fullrender.error-parsing-region-file")
    public static String COMMAND_FULLRENDER_ERROR_PARSING_REGION_FILE;

    @Key("command.help.description")
    public static String COMMAND_HELP_DESCRIPTION;

    @Key("command.hide.description")
    public static String COMMAND_HIDE_DESCRIPTION;
    @Key("command.hide.already-hidden")
    public static String COMMAND_HIDE_ALREADY_HIDDEN;
    @Key("command.hide.success")
    public static String COMMAND_HIDE_SUCCESS;

    @Key("command.reload.description")
    public static String COMMAND_RELOAD_DESCRIPTION;
    @Key("command.reload.success")
    public static String COMMAND_RELOAD_SUCCESS;

    @Key("command.show.description")
    public static String COMMAND_SHOW_DESCRIPTION;
    @Key("command.show.not-hidden")
    public static String COMMAND_SHOW_NOT_HIDDEN;
    @Key("command.show.success")
    public static String COMMAND_SHOW_SUCCESS;

    @Key("command.status.description")
    public static String COMMAND_STATUS_DESCRIPTION;

    @Key("httpd.started.success")
    public static String HTTPD_STARTED;
    @Key("httpd.started.error")
    public static String HTTPD_START_ERROR;
    @Key("httpd.stopped.success")
    public static String HTTPD_STOPPED;
    @Key("httpd.stopped.error")
    public static String HTTPD_STOP_ERROR;
    @Key("httpd.disabled")
    public static String HTTPD_DISABLED;

    @Key("error.unknown-error")
    public static String ERROR_UNKNOWN_ERROR;
    @Key("error.unknown-subcommand")
    public static String ERROR_UNKNOWN_SUBCOMMAND;
    @Key("error.must-specify-player")
    public static String ERROR_MUST_SPECIFY_PLAYER;
    @Key("error.no-such-player")
    public static String ERROR_NO_SUCH_PLAYER;
    @Key("error.must-specify-world")
    public static String ERROR_MUST_SPECIFY_WORLD;
    @Key("error.no-such-world")
    public static String ERROR_NO_SUCH_WORLD;
    @Key("error.world-disabled")
    public static String ERROR_WORLD_DISABLED;

    private static final Lang CONFIG = new Lang();

    public static void reload() {
        CONFIG.reload(LOCALE_DIR.resolve(Config.LANGUAGE_FILE), Lang.class);
    }

    public static void send(Audience recipient, String msg, TagResolver.Single... placeholders) {
        send(recipient, true, msg, placeholders);
    }

    public static void send(Audience recipient, boolean prefix, String msg, TagResolver.Single... placeholders) {
        if (msg == null) {
            return;
        }
        for (String part : msg.split("\\n")) {
            send(recipient, parse((prefix ? Lang.COMMAND_BASE_PREFIX : "") + part, placeholders));
        }
    }

    public static void send(Audience recipient, Component component) {
        recipient.sendMessage(component);
    }

    public static Component parse(String msg, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(msg, placeholders);
    }
}
