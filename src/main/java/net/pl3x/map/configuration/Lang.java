package net.pl3x.map.configuration;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.ConsoleCommandSender;

public class Lang extends AbstractConfig {
    @Key("prefix.logger")
    public static String PREFIX_LOGGER = "[<dark_aqua>Pl3xMap</dark_aqua>] ";
    @Key("prefix.command")
    public static String PREFIX_COMMAND = "[<blue>Pl3xMap</blue>] ";

    @Key("command.base.usage")
    public static String COMMAND_BASE_USAGE = "<light_purple><description>:\\n<yellow>/<grey><command> <usage>";
    @Key("command.base.subcommands.title")
    public static String COMMAND_BASE_SUBCOMMANDS_TITLE = "<color:#25b8ff>Available Commands</color><color:#9558ff>:";
    @Key("command.base.subcommands.full-command")
    public static String COMMAND_BASE_SUBCOMMANDS_FULL_COMMAND = "<color:#9558ff>/<color:#dedede><command>";
    @Key("command.base.subcommands.entry.text")
    public static String COMMAND_BASE_SUBCOMMANDS_ENTRY_TEXT = " <color:#999999><prefix> <color:#dedede><command></color> <color:#9558ff>-</color> <i><description>";
    @Key("command.base.subcommands.entry.prefix")
    public static String COMMAND_BASE_SUBCOMMANDS_ENTRY_PREFIX = "\\u251C";
    @Key("command.base.subcommands.entry.prefix-last")
    public static String COMMAND_BASE_SUBCOMMANDS_ENTRY_PREFIX_LAST = "\\u2514";

    @Key("command.cancelrender.description")
    public static String COMMAND_CANCELRENDER_DESCRIPTION = "Cancel active render of a world";
    @Key("command.cancelrender.not-rendering")
    public static String COMMAND_CANCELRENDER_NOT_RENDERING = "<red><world> does not have active render";
    @Key("command.cancelrender.success")
    public static String COMMAND_CANCELRENDER_SUCCESS = "<green>Render on <world> has been cancelled";

    @Key("command.fullrender.description")
    public static String COMMAND_FULLRENDER_DESCRIPTION = "Fully render a world";
    @Key("command.fullrender.already-rendering")
    public static String COMMAND_FULLRENDER_ALREADY_RENDERING = "<red><world> is already rendering";
    @Key("command.fullrender.starting")
    public static String COMMAND_FULLRENDER_STARTING = "<green>Starting full render of <world>";
    @Key("command.fullrender.obtaining-regions")
    public static String COMMAND_FULLRENDER_OBTAINING_REGIONS = "<yellow>Obtaining region files";
    @Key("command.fullrender.sorting-regions")
    public static String COMMAND_FULLRENDER_SORTING_REGIONS = "<yellow>Sorting region files";
    @Key("command.fullrender.found-total-regions")
    public static String COMMAND_FULLRENDER_FOUND_TOTAL_REGIONS = "<green>Found <gray><total> <green>region files";
    @Key("command.fullrender.use-status-for-progress")
    public static String COMMAND_FULLRENDER_USE_STATUS_FOR_PROGRESS = "<gold>Use <grey>/status</grey> command to view progress";
    @Key("command.fullrender.error-parsing-region-file")
    public static String COMMAND_FULLRENDER_ERROR_PARSING_REGION_FILE = "Failed to parse coordinates for region file '<path>' (<filename>)";

    @Key("command.help.description")
    public static String COMMAND_HELP_DESCRIPTION = "Get help for Pl3xmap commands";

    @Key("command.hide.description")
    public static String COMMAND_HIDE_DESCRIPTION = "Hide a player from the map";
    @Key("command.hide.already-hidden")
    public static String COMMAND_HIDE_ALREADY_HIDDEN = "<red><player> is already hidden from map";
    @Key("command.hide.success")
    public static String COMMAND_HIDE_SUCCESS = "<green><player> is now hidden from map";

    @Key("command.reload.description")
    public static String COMMAND_RELOAD_DESCRIPTION = "Reloads the plugin";
    @Key("command.reload.success")
    public static String COMMAND_RELOAD_SUCCESS = "<green>Pl3xMap v<version> reloaded";

    @Key("command.show.description")
    public static String COMMAND_SHOW_DESCRIPTION = "Show a player on the map";
    @Key("command.show.not-hidden")
    public static String COMMAND_SHOW_NOT_HIDDEN = "<red><player> is not hidden from map";
    @Key("command.show.success")
    public static String COMMAND_SHOW_SUCCESS = "<green><player> is no longer hidden from map";

    @Key("command.status.description")
    public static String COMMAND_STATUS_DESCRIPTION = "View a world's render status";
    @Key("command.status.already-rendering")
    public static String COMMAND_STATUS_NOT_RENDERING = "<gold>Map status of <world>\\n" +
            "Background: <green>Running\\n" +
            "Active: <red>Not Running";
    @Key("command.status.success")
    public static String COMMAND_STATUS_RENDERING = "<gold>Map status of <world>\\n" +
            "Background: <red>Paused\\n" +
            "Active: <green><status>\\n" +
            "  <gray>- Chunks Rendered <chunk_done>/<chunks_total> (<gold><percent>%</gold>)\\n" +
            "  <gray>- Time Remaining: <remaining> (<gold><cps> cps</gold>)";
    @Key("command.status.player-only-feature")
    public static String COMMAND_STATUS_PLAYER_ONLY_FEATURE = "<red>That is a player only feature";

    @Key("httpd.started.success")
    public static String HTTPD_STARTED = "<green>Internal webserver running on <yellow><bind></yellow>:<yellow><port></yellow>";
    @Key("httpd.started.error")
    public static String HTTPD_START_ERROR = "<red>Internal webserver could not start";
    @Key("httpd.stopped.success")
    public static String HTTPD_STOPPED = "<green>Internal webserver stopped";
    @Key("httpd.stopped.error")
    public static String HTTPD_STOP_ERROR = "<red>An error occurred with the internal webserver";
    @Key("httpd.disabled")
    public static String HTTPD_DISABLED = "<green>Internal webserver is disabled in config.yml";

    @Key("error.unknown-error")
    public static String ERROR_UNKNOWN_ERROR = "<red>Unknown error";
    @Key("error.unknown-subcommand")
    public static String ERROR_UNKNOWN_SUBCOMMAND = "<red>Unknown subcommand";
    @Key("error.must-specify-player")
    public static String ERROR_MUST_SPECIFY_PLAYER = "<red>You must specify the player";
    @Key("error.no-such-player")
    public static String ERROR_NO_SUCH_PLAYER = "<red>No such player";
    @Key("error.must-specify-world")
    public static String ERROR_MUST_SPECIFY_WORLD = "<red>You must specify the world";
    @Key("error.no-such-world")
    public static String ERROR_NO_SUCH_WORLD = "<red>No such world";
    @Key("error.world-disabled")
    public static String ERROR_WORLD_DISABLED = "<red>Pl3xMap is disabled in this world";

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
        for (String part : msg.split("\n")) {
            send(recipient, prefix, parse(part, placeholders));
        }
    }

    public static void send(Audience recipient, Component component) {
        send(recipient, true, component);
    }

    public static void send(Audience recipient, boolean prefix, Component component) {
        if (recipient instanceof ConsoleCommandSender) {
            recipient.sendMessage(prefix ? parse(Lang.PREFIX_LOGGER).append(component) : component);
        } else {
            recipient.sendMessage(prefix ? parse(Lang.PREFIX_COMMAND).append(component) : component);
        }
    }

    public static Component parse(String msg, TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(msg, placeholders);
    }
}
