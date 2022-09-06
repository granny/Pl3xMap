package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;

public class BukkitLang extends BukkitAbstractConfig {
    private static final BukkitLang CONFIG = new BukkitLang();

    public static void reload() {
        // extract locale from jar
        FileUtil.extract("/locale/", FileUtil.LOCALE_DIR, false);

        CONFIG.reload(FileUtil.LOCALE_DIR.resolve(Config.LANGUAGE_FILE), Lang.class);
    }
}
