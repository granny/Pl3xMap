package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;

public class BukkitConfig extends BukkitAbstractConfig {
    private static final BukkitConfig CONFIG = new BukkitConfig();

    public static void reload() {
        CONFIG.reload(FileUtil.MAIN_DIR.resolve("config.yml"), Config.class);
    }
}
