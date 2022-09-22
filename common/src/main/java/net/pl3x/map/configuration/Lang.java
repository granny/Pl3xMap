package net.pl3x.map.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.pl3x.map.util.FileUtil;
import org.jetbrains.annotations.NotNull;

public final class Lang extends AbstractConfig {
    @Key("prefix.command")
    public static String PREFIX_COMMAND = "<white>[<gradient:#C028FF:#5B00FF>Pl3xMap</gradient>]</white> ";

    @Key("command.event.click-for-help")
    public static String CLICK_FOR_HELP = "Click for help";
    @Key("command.event.click-to-confirm")
    public static String CLICK_TO_CONFIRM = "Click to confirm";

    @Key("addon.enabling-all")
    public static String ADDONS_ENABLING = "Enabling addons";
    @Key("addon.disabling-all")
    public static String ADDONS_DISABLING = "Disabling addons";
    @Key("addon.enabling")
    public static String ADDON_ENABLING = " - <name> <version>";
    @Key("addon.disabling")
    public static String ADDON_DISABLING = " - <name> <version>";

    @Key("command.argument.help-query")
    public static String COMMAND_ARGUMENT_HELP_QUERY_DESCRIPTION = "Help Query";
    @Key("command.argument.optional-player")
    public static String COMMAND_ARGUMENT_OPTIONAL_PLAYER_DESCRIPTION = "Defaults to the executing player if unspecified (console must specify a player)";
    @Key("command.argument.optional-world")
    public static String COMMAND_ARGUMENT_OPTIONAL_WORLD_DESCRIPTION = "Defaults to the players current world if not provided";
    @Key("command.argument.optional-center")
    public static String COMMAND_ARGUMENT_OPTIONAL_CENTER_DESCRIPTION = "Defaults to (<white>0<gray>,</gray> 0</white>) if unspecified";

    @Key("command.addon.description")
    public static String COMMAND_ADDON_DESCRIPTION = "Manage Pl3xMap addons";

    @Key("command.cancelrender.description")
    public static String COMMAND_CANCELRENDER_DESCRIPTION = "Cancel active render of a world";
    @Key("command.cancelrender.not-rendering")
    public static String COMMAND_CANCELRENDER_NOT_RENDERING = "<grey><world> <red>does not have active render";
    @Key("command.cancelrender.success")
    public static String COMMAND_CANCELRENDER_SUCCESS = "<green>Render on <grey><world> </grey>has been cancelled";

    @Key("command.confirm.description")
    public static String COMMAND_CONFIRM_DESCRIPTION = "Confirm a pending command";
    @Key("command.confirm.not-rendering")
    public static String COMMAND_CONFIRM_CONFIRMATION_REQUIRED_MESSAGE = "<red>Confirmation required. Confirm using <grey>/map confirm";
    @Key("command.confirm.success")
    public static String COMMAND_CONFIRM_NO_PENDING_MESSAGE = "<red>You don't have any pending confirmations";

    @Key("command.fullrender.description")
    public static String COMMAND_FULLRENDER_DESCRIPTION = "Fully render a world";
    @Key("command.fullrender.already-rendering")
    public static String COMMAND_FULLRENDER_ALREADY_RENDERING = "<grey><world> <red>is already rendering";
    @Key("command.fullrender.starting")
    public static String COMMAND_FULLRENDER_STARTING = "<green>Starting full render of <grey><world>";
    @Key("command.fullrender.finished")
    public static String COMMAND_FULLRENDER_FINISHED = "<green>Finished full render on <grey><world></grey> in <grey><elapsed>";
    @Key("command.fullrender.cancelled")
    public static String COMMAND_FULLRENDER_CANCELLED = "<red>Cancelled full render on <grey><world>";
    @Key("command.fullrender.resumed-rendering")
    public static String COMMAND_FULLRENDER_RESUMED_RENDERING = "<yellow>Previously incomplete full render for <grey><world></grey> has resumed where it left off";
    @Key("command.fullrender.obtaining-regions")
    public static String COMMAND_FULLRENDER_OBTAINING_REGIONS = "<yellow>Obtaining regions from files... (this may take a moment)";
    @Key("command.fullrender.found-total-regions")
    public static String COMMAND_FULLRENDER_FOUND_TOTAL_REGIONS = "<green>Found <grey><total></grey> region files";
    @Key("command.fullrender.resumed-total-regions")
    public static String COMMAND_FULLRENDER_RESUMED_TOTAL_REGIONS = "<green>Resumed scanning <grey><done></grey>/<grey><total></grey> regions (<gold><percent>%</gold>)";
    @Key("command.fullrender.use-status-for-progress")
    public static String COMMAND_FULLRENDER_USE_STATUS_FOR_PROGRESS = "<gold>Use <grey>/map status</grey> command to view progress";
    @Key("command.fullrender.error-parsing-region-file")
    public static String COMMAND_FULLRENDER_ERROR_PARSING_REGION_FILE = "Failed to parse coordinates for region file <grey><path> (<filename>)";

    @Key("command.help.description")
    public static String COMMAND_HELP_DESCRIPTION = "Get help for Pl3xmap commands";

    @Key("command.hide.description")
    public static String COMMAND_HIDE_DESCRIPTION = "Hide a player from the map";
    @Key("command.hide.already-hidden")
    public static String COMMAND_HIDE_ALREADY_HIDDEN = "<grey><player> <red>is already hidden from the map";
    @Key("command.hide.success")
    public static String COMMAND_HIDE_SUCCESS = "<grey><player> <green>is now hidden from the map";

    @Key("command.pauserender.description")
    public static String COMMAND_PAUSERENDER_DESCRIPTION = "Pauses all renders for the specified world";
    @Key("command.pauserender.paused")
    public static String COMMAND_PAUSERENDER_PAUSED = "<green>Paused renders for <grey><world>";
    @Key("command.pauserender.resumed")
    public static String COMMAND_PAUSERENDER_RESUMED = "<green>Resumed renders for <grey><world>";

    @Key("command.radiusrender.description")
    public static String COMMAND_RADIUSRENDER_DESCRIPTION = "Render a section of a world";
    @Key("command.radiusrender.already-rendering")
    public static String COMMAND_RADIUSRENDER_ALREADY_RENDERING = "<grey><world> <red>is already rendering";
    @Key("command.radiusrender.starting")
    public static String COMMAND_RADIUSRENDER_STARTING = "<green>Starting radius render of <grey><world>";
    @Key("command.radiusrender.finished")
    public static String COMMAND_RADIUSRENDER_FINISHED = "<green>Finished radius render on <grey><world></grey> in <grey><elapsed>";
    @Key("command.radiusrender.cancelled")
    public static String COMMAND_RADIUSRENDER_CANCELLED = "<red>Cancelled radius render on <grey><world>";
    @Key("command.radiusrender.obtaining-regions")
    public static String COMMAND_RADIUSRENDER_OBTAINING_CHUNKS = "<yellow>Obtaining chunks in radius... (this may take a moment)";
    @Key("command.radiusrender.found-total-regions")
    public static String COMMAND_RADIUSRENDER_FOUND_TOTAL_CHUNKS = "<green>Found <grey><total></grey> chunks in radius";
    @Key("command.radiusrender.use-status-for-progress")
    public static String COMMAND_RADIUSRENDER_USE_STATUS_FOR_PROGRESS = "<gold>Use <grey>/map status</grey> command to view progress";
    @Key("command.radiusrender.error-parsing-region-file")
    public static String COMMAND_RADIUSRENDER_ERROR_PARSING_REGION_FILE = "Failed to parse coordinates for region file '<path>' (<filename>)";

    @Key("command.reload.description")
    public static String COMMAND_RELOAD_DESCRIPTION = "Reloads the plugin";
    @Key("command.reload.success")
    public static String COMMAND_RELOAD_SUCCESS = "<green>Pl3xMap <grey>v<version></grey> reloaded";

    @Key("command.resetmap.description")
    public static String COMMAND_RESETMAP_DESCRIPTION = "Cancel active render of a world";
    @Key("command.resetmap.active-render")
    public static String COMMAND_RESETMAP_ACTIVE_RENDER = "<red>There is an active render for <grey><world>";
    @Key("command.resetmap.success")
    public static String COMMAND_RESETMAP_SUCCESS = "<green>Successfully reset map for <grey><world>";
    @Key("command.resetmap.failed")
    public static String COMMAND_RESETMAP_FAILED = "<red>Could not reset map for <grey><world>";

    @Key("command.show.description")
    public static String COMMAND_SHOW_DESCRIPTION = "Show a player on the map";
    @Key("command.show.not-hidden")
    public static String COMMAND_SHOW_NOT_HIDDEN = "<grey><player> <red>is not hidden from the map";
    @Key("command.show.success")
    public static String COMMAND_SHOW_SUCCESS = "<grey><player> <green>is no longer hidden from the map";

    @Key("command.status.description")
    public static String COMMAND_STATUS_DESCRIPTION = "View a world's render status";
    @Key("command.status.already-rendering")
    public static String COMMAND_STATUS_NOT_RENDERING = """
            <gold>Map status of <world>
            Background: <green>Running
            Active: <red>Not Running""";
    @Key("command.status.render")
    public static String COMMAND_STATUS_RENDER = """
            <green>Map status of <gold><world>
            <white>  Background: <i><background>
            <white>  Foreground: <i><foreground>""";
    @Key("command.status.render-details")
    public static String COMMAND_STATUS_RENDER_DETAILS = """
            <grey>    Chunks: <chunks_done>/<chunks_total> (<gold><percent>%</gold>)
            <grey>    Remaining: <remaining> (<gold><cps> cps</gold>)""";
    @Key("command.status.render-running")
    public static String COMMAND_STATUS_RENDER_RUNNING = "<green>Running";
    @Key("command.status.render-not-running")
    public static String COMMAND_STATUS_RENDER_NOT_RUNNING = "<red>Not Running";
    @Key("command.status.render-paused")
    public static String COMMAND_STATUS_RENDER_PAUSED = "<red>Paused";
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
    public static String HTTPD_DISABLED = "<green>Internal webserver is disabled in <grey>config.yml";

    @Key("progress.chat")
    public static String PROGRESS_CHAT = "<world>: <processed_chunks>/<total_chunks> (<percent>%) <gold><cps> cps</gold> eta: <eta>";
    @Key("progress.bossbar")
    public static String PROGRESS_BOSSBAR = "<gold>Map render of <grey><world></grey>: <red><percent></red>% eta: <eta>";
    @Key("progress.eta.unknown")
    public static String PROGRESS_ETA_UNKNOWN = "Unknown";

    @Key("error.must-specify-player")
    public static String ERROR_MUST_SPECIFY_PLAYER = "<red>You must specify the player";
    @Key("error.no-such-player")
    public static String ERROR_NO_SUCH_PLAYER = "<red>No such player <grey><player>";
    @Key("error.must-specify-world")
    public static String ERROR_MUST_SPECIFY_WORLD = "<red>You must specify the world";
    @Key("error.no-such-world")
    public static String ERROR_NO_SUCH_WORLD = "<red>No such world <grey><world>";
    @Key("error.must-specify-center")
    public static String ERROR_MUST_SPECIFY_CENTER = "<red>You must specify the center point";
    @Key("error.point-invalid-format")
    public static String ERROR_POINT_INVALID_FORMAT = "'<point>' is not a valid location. Required format is '<x> <z>'";
    @Key("error.world-disabled")
    public static String ERROR_WORLD_DISABLED = "<red>Pl3xMap is disabled for world <grey><world>";
    @Key("error.render-stalled")
    public static String ERROR_RENDER_STALLED = "<red>Render has stalled! Cancelling.";

    @Key("ui.layer.players")
    public static String UI_LAYER_PLAYERS = "World Border";
    @Key("ui.layer.spawn")
    public static String UI_LAYER_SPAWN = "Spawn";
    @Key("ui.layer.worldborder")
    public static String UI_LAYER_WORLDBORDER = "World Border";

    @Key("ui.title")
    public static String UI_TITLE = "Pl3xMap";
    @Key("ui.blockinfo.label")
    public static String UI_BLOCKINFO_LABEL = "BlockInfo";
    @Key("ui.blockinfo.value")
    public static String UI_BLOCKINFO_VALUE = "<block><br /><biome>";
    @Key("ui.coords.label")
    public static String UI_COORDS_LABEL = "Coordinates";
    @Key("ui.coords.value")
    public static String UI_COORDS_VALUE = "<x>, <y>, <z>";
    @Key("ui.link.label")
    public static String UI_LINK_LABEL = "Sharable Link";
    @Key("ui.link.value")
    public static String UI_LINK_VALUE = "";
    @Key("ui.markers.label")
    public static String UI_MARKERS_LABEL = "Markers";
    @Key("ui.markers.value")
    public static String UI_MARKERS_VALUE = "No markers have been configured";
    @Key("ui.players.label")
    public static String UI_PLAYERS_LABEL = "Players (<online>/<max>)";
    @Key("ui.players.value")
    public static String UI_PLAYERS_VALUE = "No players are currently online";
    @Key("ui.worlds.label")
    public static String UI_WORLDS_LABEL = "Worlds";
    @Key("ui.worlds.value")
    public static String UI_WORLDS_VALUE = "No worlds have been configured";
    @Key("ui.layers.label")
    public static String UI_LAYERS_LABEL = "Layers";
    @Key("ui.layers.value")
    public static String UI_LAYERS_VALUE = "No layers have been configured";

    private static final Lang CONFIG = new Lang();

    public static void reload() {
        // extract locale from jar
        FileUtil.extract("/locale/", FileUtil.LOCALE_DIR, false);

        CONFIG.reload(FileUtil.LOCALE_DIR.resolve(Config.LANGUAGE_FILE), Lang.class);
    }

    public static Component parse(@NotNull String msg, @NotNull TagResolver.Single... placeholders) {
        return MiniMessage.miniMessage().deserialize(msg, placeholders);
    }
}
