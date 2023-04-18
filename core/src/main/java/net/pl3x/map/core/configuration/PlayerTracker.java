package net.pl3x.map.core.configuration;

import net.pl3x.map.core.Pl3xMap;

public final class PlayerTracker extends AbstractConfig {
    @Key("settings.enabled")
    @Comment("Show online players on the map.")
    public static boolean ENABLED = true;

    @Key("settings.pane")
    @Comment("""
            The custom pane layer for the player tracker.
            This is used to make custom css styled tooltips.""")
    public static String PANE = "nameplates";

    @Key("settings.icon")
    @Comment("""
            The player icon.
            Icon must be in the web/images/icon/ directory.""")
    public static String ICON = "players";

    @Key("settings.hide.invisible")
    @Comment("Should invisible players be hidden from the map")
    public static boolean HIDE_INVISIBLE = true;

    @Key("settings.hide.spectators")
    @Comment("Should spectators be hidden from the map")
    public static boolean HIDE_SPECTATORS = true;

    @Key("settings.tooltip")
    @Comment("""
            Tooltip for player markers.
            Variables: <uuid><name><decoratedName><health><armor>""")
    public static String TOOLTIP = """
            <ul>
              <li><img src='images/skins/2D/<uuid>.png' class='head' alt='<name>' /></li>
              <li>
                <name>
                <img src='images/clear.png' class='health' style='background-position:0 calc(-<health>px * 9);' alt='Health <health>' />
                <img src='images/clear.png' class='armor' style='background-position:0 calc(-<armor>px * 9);' alt='Armor <armor>' />
              </li>
            </ul>""";

    @Key("settings.css")
    @Comment("""
            Custom css for players marker layer.
            Class names use the pane variable in the name.""")
    public static String CSS = """
            div.leaflet-nameplates-pane div img.head {
              image-rendering: pixelated;
              image-rendering: -moz-crisp-edges;
              -ms-interpolation-mode: nearest-neighbor;
            }
            div.leaflet-nameplates-pane div {
              margin: 0;
              padding: 0;
              color: #ffffff;
              font-weight: 700;
              line-height: 1rem;
              background: rgba(0, 0, 0, 0.5);
              border-color: rgba(0, 0, 0, 0.75);
            }
            div.leaflet-nameplates-pane div ul {
              padding: 3px;
            }
            div.leaflet-nameplates-pane div:before {
              border-color: transparent;
            }
            div.leaflet-nameplates-pane div img.head {
              vertical-align: middle;
              width: 32px;
              height: 32px;
              border-radius: 5px;
              border: 1px solid black;
            }
            div.leaflet-nameplates-pane div img.health {
              margin-top: 3px;
            }
            div.leaflet-nameplates-pane div img.armor,
            div.leaflet-nameplates-pane div img.health {
              display: block;
              width: 81px;
              height: 9px;
              background-position: 0 0;
            }
            div.leaflet-nameplates-pane div img.armor {
              background: url('images/armor.png') no-repeat;
            }
            div.leaflet-nameplates-pane div img.health {
              background: url('images/health.png') no-repeat;
            }
            div.leaflet-nameplates-pane div,
            div.leaflet-marker-pane img {
              transition: all 0.25s;
            }""";

    private static final PlayerTracker CONFIG = new PlayerTracker();

    public static void reload() {
        CONFIG.reload(Pl3xMap.api().getMainDir().resolve("player-tracker.yml"), PlayerTracker.class);
    }
}
