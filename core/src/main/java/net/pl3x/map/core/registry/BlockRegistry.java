package net.pl3x.map.core.registry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import net.pl3x.map.core.log.Logger;
import net.pl3x.map.core.util.FileUtil;
import net.pl3x.map.core.world.Block;
import net.pl3x.map.core.world.Blocks;
import org.checkerframework.checker.nullness.qual.NonNull;

public class BlockRegistry extends Registry<@NonNull Block> {
    private static final Gson GSON = new GsonBuilder().create();

    public @NonNull Block register(@NonNull String id, int color) {
        Block block = super.get(id);
        if (block != null) {
            return block; // block already registered
        }
        if (id.startsWith("minecraft:")) {
            Logger.warn("Registering unknown vanilla block " + id);
        }
        return register(id, new Block(size(), id, color)); // todo - use old index from disk
    }

    @Override
    public @NonNull Block get(@NonNull String id) {
        return getOrDefault(id, Blocks.AIR);
    }

    public void saveToDisk() {
        Map<Integer, String> map = new HashMap<>();
        values().forEach(block -> map.put(block.getIndex(), block.getKey()));
        try {
            FileUtil.saveGzip(GSON.toJson(map), FileUtil.getTilesDir().resolve("blocks.gz"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
