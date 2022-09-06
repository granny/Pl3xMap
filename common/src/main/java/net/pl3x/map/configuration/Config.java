package net.pl3x.map.configuration;

public abstract class Config extends AbstractConfig {
    @Key("settings.debug-mode")
    @Comment("""
            Extra logger output.
            (can be spammy)""")
    public static boolean DEBUG_MODE = false;
    @Key("settings.language-file")
    @Comment("The language file to use from the locale folder.")
    public static String LANGUAGE_FILE = "lang-en.yml";

    @Key("settings.web-directory.path")
    @Comment("""
            The directory that houses the website and world tiles.
            This is a relative path from Pl3xMap's plugin directory,
            unless it starts with / in which case it will be treated
            as an absolute path.""")
    public static String WEB_DIR = "web";
    @Key("settings.web-directory.read-only")
    @Comment("""
            Set to true if you don't want Pl3xMap to overwrite
            the website files on startup. (Good for servers that
            customize these files)""")
    public static boolean WEB_DIR_READONLY = false;
    @Key("settings.web-directory.tile-format")
    @Comment("""
            The image format for tile images.
            Built in types: png""")
    public static String WEB_TILE_FORMAT = "png";

    @Key("settings.internal-webserver.enabled")
    @Comment("""
            Enable the built-in web server.
            Disable this if you want to use a standalone web server such as apache or nginx.""")
    public static boolean HTTPD_ENABLED = true;
    @Key("settings.internal-webserver.bind")
    @Comment("""
            The interface IP the built-in web server should bind to.
            This is NOT always the same as your public facing IP.
            If you don't understand what this is, leave it set to 0.0.0.0""")
    public static String HTTPD_BIND = "0.0.0.0";
    @Key("settings.internal-webserver.port")
    @Comment("""
            The port the built-in web server listens to.
            Make sure the port is allocated if using Pterodactyl.""")
    public static int HTTPD_PORT = 8080;
}
