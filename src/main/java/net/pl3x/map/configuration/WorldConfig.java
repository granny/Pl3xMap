package net.pl3x.map.configuration;

import net.pl3x.map.util.FileUtil;
import org.bukkit.World;

import java.util.List;

public class WorldConfig extends AbstractConfig {
    @Key("renderer")
    public String renderer;

    private final String worldName;

    public WorldConfig(World world) {
        this.worldName = world.getName();
    }

    public void reload() {
        setHeader(List.of(
                "This is the worlds configuration file for Pl3xMap.",
                "",
                "If you need help with the configuration or have any",
                "questions related to Pl3xMap, join us in our Discord",
                "",
                "Discord: https://discord.gg/nhGzEkwXQX",
                "Wiki: https://github.com/pl3xgaming/Pl3xMap/wiki"
        ));
        reload(FileUtil.PLUGIN_DIR.resolve("worlds.yml").toFile(), WorldConfig.class);
    }

    @Override
    protected String getKeyValue(Key key) {
        return "worlds." + this.worldName + "." + key.value();
    }
}
