package net.pl3x.map.configuration;

import java.util.List;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;

public class BukkitWorldConfig extends BukkitAbstractConfig {
    private final World world;
    private final WorldConfig worldConfig;

    public BukkitWorldConfig(World world) {
        this.world = world;
        this.worldConfig = new WorldConfig();
    }

    public WorldConfig reload() {
        reload(FileUtil.MAIN_DIR.resolve("config.yml"), WorldConfig.class);
        this.worldConfig.reload();
        return this.worldConfig;
    }

    @Override
    protected Object getClassObject() {
        return this.worldConfig;
    }

    @Override
    protected Object getValue(String path, Object def) {
        if (getConfig().get("world-settings.default." + path) == null) {
            getConfig().set("world-settings.default." + path, def);
        }

        return getConfig().get("world-settings." + this.world.getName() + "." + path,
                getConfig().get("world-settings.default." + path));
    }

    @Override
    protected void setComments(String path, List<String> comments) {
        getConfig().setComments("world-settings.default." + path, comments);
    }
}
