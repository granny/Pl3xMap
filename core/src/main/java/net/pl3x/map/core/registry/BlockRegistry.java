/*
 * MIT License
 *
 * Copyright (c) 2020 William Blake Galbreath
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
