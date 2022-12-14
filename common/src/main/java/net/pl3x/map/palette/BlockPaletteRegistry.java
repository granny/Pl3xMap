package net.pl3x.map.palette;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.pl3x.map.util.FileUtil;
import net.pl3x.map.world.World;

public class BlockPaletteRegistry extends PaletteRegistry<Block> {
    private static final Gson GSON = new GsonBuilder()
            //.setPrettyPrinting()
            .disableHtmlEscaping()
            .serializeNulls()
            .setLenient()
            .create();

    public BlockPaletteRegistry() {
        // create block palette
        BuiltInRegistries.BLOCK.forEach(block -> {
            String name = PaletteRegistry.toName("block", BuiltInRegistries.BLOCK.getKey(block));
            Palette palette = new Palette(size(), name);
            register(block, palette);
        });
        lock();

        // save global block palette
        try {
            FileUtil.saveGzip(GSON.toJson(getMap()), World.TILES_DIR.resolve("blocks.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
